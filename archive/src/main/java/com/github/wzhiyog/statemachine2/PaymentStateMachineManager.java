package com.github.wzhiyog.statemachine2;

/**
 * 支付状态机管理器
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Component
public class PaymentStateMachineManager {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 执行状态转换
     *
     * @param paymentId 支付ID
     * @param event     触发事件
     * @return 转换结果
     */
    public PaymentStateChangeResult executeTransition(String paymentId, PaymentEvent event) {
        return transactionTemplate.execute(status -> {
            try {
                // 一锁：获取数据库行锁（通过for update实现）
                Payment payment = paymentRepository.findByIdWithLock(paymentId);
                if (payment == null) {
                    return new PaymentStateChangeResult(false, "支付记录不存在");
                }

                // 二判：判断状态转换是否合法
                PaymentStatus currentStatus = payment.getStatus();
                if (!PaymentStateTransition.canTransfer(currentStatus, event)) {
                    return new PaymentStateChangeResult(false, String.format("不允许的状态转换：从 %s 到 %s", currentStatus.getDisplayName(), event.getDescription()));
                }

                // 获取目标状态
                PaymentStatus targetStatus = PaymentStateTransition.getNextStatus(currentStatus, event);

                // 执行业务逻辑
                executeBusinessLogic(payment, currentStatus, targetStatus, event);

                // 三更新：更新状态
                payment.setStatus(targetStatus);
                payment.setUpdateTime(new Date());
                paymentRepository.save(payment);

                // 发布状态变更事件
//                publishStateChangeEvent(payment, currentStatus, targetStatus, event);

                return new PaymentStateChangeResult(true, "状态转换成功", targetStatus);

            } catch (Exception e) {
                status.setRollbackOnly();
                return new PaymentStateChangeResult(false, "状态转换异常：" + e.getMessage());
            }
        });
    }

    /**
     * 执行状态转换相关的业务逻辑
     */
    private void executeBusinessLogic(Payment payment, PaymentStatus currentStatus, PaymentStatus targetStatus, PaymentEvent event) {
        // 根据不同的状态转换执行不同的业务逻辑
        switch (event) {
            case PAY:
                // 处理发起支付逻辑
                break;
            case PAYMENT_SUCCESS:
                // 处理支付成功逻辑
                break;
            case REQUEST_REFUND:
                // 处理退款申请逻辑
                break;
            // 其他事件处理...
        }
    }

    /**
     * 发布状态变更事件
     */
//    private void publishStateChangeEvent(Payment payment, PaymentStatus previousStatus,
//                                         PaymentStatus currentStatus, PaymentEvent event) {
//        PaymentStateChangeEvent stateChangeEvent = new PaymentStateChangeEvent(
//                payment.getId(), previousStatus, currentStatus, event, new Date());
//        eventPublisher.publishEvent(stateChangeEvent);
//    }

    /**
     * 状态转换结果类
     */
    public static class PaymentStateChangeResult {
        private final boolean success;
        private final String message;
        private PaymentStatus targetStatus;

        public PaymentStateChangeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public PaymentStateChangeResult(boolean success, String message, PaymentStatus targetStatus) {
            this.success = success;
            this.message = message;
            this.targetStatus = targetStatus;
        }

        // getter方法...
    }
}