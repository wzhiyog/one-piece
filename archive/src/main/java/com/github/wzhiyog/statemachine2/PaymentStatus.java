package com.github.wzhiyog.statemachine2;


/**
 * 支付交易状态枚举
 */
public enum PaymentStatus {

    CREATED("已创建", "交易已创建，等待用户支付"),

    PROCESSING("处理中", "用户已发起支付，等待支付结果"),

    SUCCESS("支付成功", "交易支付成功"),

    FAILED("支付失败", "交易支付失败"),

    CLOSED("已关闭", "交易已关闭"),

    REFUND_PROCESSING("退款处理中", "退款申请已提交，等待处理"),

    REFUND_SUCCESS("退款成功", "退款已成功处理"),

    REFUND_FAILED("退款失败", "退款处理失败");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
