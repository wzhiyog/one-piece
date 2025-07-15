package com.github.wzhiyog.statemachine2;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态转换规则定义
 */
public class PaymentStateTransition {

    // 使用Map存储状态转换规则
    private static final Map<PaymentStatus, Map<PaymentEvent, PaymentStatus>> STATE_MACHINE_MAP = new HashMap<>();

    static {
        // 初始化状态转换规则

        // CREATED状态下的转换规则
        Map<PaymentEvent, PaymentStatus> createdTransitions = new HashMap<>();
        createdTransitions.put(PaymentEvent.PAY, PaymentStatus.PROCESSING);
        createdTransitions.put(PaymentEvent.CLOSE, PaymentStatus.CLOSED);
        STATE_MACHINE_MAP.put(PaymentStatus.CREATED, createdTransitions);

        // PROCESSING状态下的转换规则
        Map<PaymentEvent, PaymentStatus> processingTransitions = new HashMap<>();
        processingTransitions.put(PaymentEvent.PAYMENT_SUCCESS, PaymentStatus.SUCCESS);
        processingTransitions.put(PaymentEvent.PAYMENT_FAILED, PaymentStatus.FAILED);
        STATE_MACHINE_MAP.put(PaymentStatus.PROCESSING, processingTransitions);

        // SUCCESS状态下的转换规则
        Map<PaymentEvent, PaymentStatus> successTransitions = new HashMap<>();
        successTransitions.put(PaymentEvent.REQUEST_REFUND, PaymentStatus.REFUND_PROCESSING);
        STATE_MACHINE_MAP.put(PaymentStatus.SUCCESS, successTransitions);

        // FAILED状态下的转换规则
        Map<PaymentEvent, PaymentStatus> failedTransitions = new HashMap<>();
        failedTransitions.put(PaymentEvent.CLOSE, PaymentStatus.CLOSED);
        STATE_MACHINE_MAP.put(PaymentStatus.FAILED, failedTransitions);

        // REFUND_PROCESSING状态下的转换规则
        Map<PaymentEvent, PaymentStatus> refundProcessingTransitions = new HashMap<>();
        refundProcessingTransitions.put(PaymentEvent.REFUND_SUCCESS, PaymentStatus.REFUND_SUCCESS);
        refundProcessingTransitions.put(PaymentEvent.REFUND_FAILED, PaymentStatus.REFUND_FAILED);
        STATE_MACHINE_MAP.put(PaymentStatus.REFUND_PROCESSING, refundProcessingTransitions);
    }

    /**
     * 获取下一个状态
     *
     * @param currentStatus 当前状态
     * @param event         触发事件
     * @return 下一个状态，如果转换不合法则返回null
     */
    public static PaymentStatus getNextStatus(PaymentStatus currentStatus, PaymentEvent event) {
        Map<PaymentEvent, PaymentStatus> transitions = STATE_MACHINE_MAP.get(currentStatus);
        return transitions != null ? transitions.get(event) : null;
    }

    /**
     * 判断状态转换是否合法
     *
     * @param currentStatus 当前状态
     * @param event         触发事件
     * @return 是否合法
     */
    public static boolean canTransfer(PaymentStatus currentStatus, PaymentEvent event) {
        return getNextStatus(currentStatus, event) != null;
    }
}