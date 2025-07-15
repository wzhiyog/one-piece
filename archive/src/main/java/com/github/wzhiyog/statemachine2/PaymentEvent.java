package com.github.wzhiyog.statemachine2;

/**
 * 支付事件枚举
 */
public enum PaymentEvent {

    CREATE("创建交易"),
    PAY("发起支付"),
    PAYMENT_SUCCESS("支付成功通知"),
    PAYMENT_FAILED("支付失败通知"),
    CLOSE("关闭交易"),
    REQUEST_REFUND("申请退款"),
    REFUND_SUCCESS("退款成功通知"),
    REFUND_FAILED("退款失败通知");

    private final String description;

    PaymentEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}