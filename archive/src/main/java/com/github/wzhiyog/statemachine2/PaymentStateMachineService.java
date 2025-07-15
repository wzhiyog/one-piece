package com.github.wzhiyog.statemachine2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 支付状态机服务
 */
@Service
public class PaymentStateMachineService {

    @Autowired
    private PaymentStateMachineManager stateMachineManager;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * 创建支付交易
     */
    public Payment createPayment(PaymentCreateRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreateTime(new Date());
        payment.setUpdateTime(new Date());

        return paymentRepository.save(payment);
    }

    /**
     * 发起支付
     */
    public PaymentStateMachineManager.PaymentStateChangeResult pay(String paymentId) {
        return stateMachineManager.executeTransition(paymentId, PaymentEvent.PAY);
    }

    /**
     * 处理支付结果通知
     */
    public PaymentStateMachineManager.PaymentStateChangeResult processPaymentResult(
            String paymentId, boolean success) {
        PaymentEvent event = success ? PaymentEvent.PAYMENT_SUCCESS : PaymentEvent.PAYMENT_FAILED;
        return stateMachineManager.executeTransition(paymentId, event);
    }

    /**
     * 申请退款
     */
    public PaymentStateMachineManager.PaymentStateChangeResult requestRefund(String paymentId) {
        return stateMachineManager.executeTransition(paymentId, PaymentEvent.REQUEST_REFUND);
    }

    /**
     * 处理退款结果通知
     */
    public PaymentStateMachineManager.PaymentStateChangeResult processRefundResult(
            String paymentId, boolean success) {
        PaymentEvent event = success ? PaymentEvent.REFUND_SUCCESS : PaymentEvent.REFUND_FAILED;
        return stateMachineManager.executeTransition(paymentId, event);
    }

    /**
     * 关闭交易
     */
    public PaymentStateMachineManager.PaymentStateChangeResult closePayment(String paymentId) {
        return stateMachineManager.executeTransition(paymentId, PaymentEvent.CLOSE);
    }
}