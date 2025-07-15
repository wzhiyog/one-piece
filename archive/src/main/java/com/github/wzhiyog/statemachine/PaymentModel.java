package com.github.wzhiyog.statemachine;

/**
 * 支付单模型
 */
public class PaymentModel {

    // 上次状态
    private PaymentStatus lastStatus;
    // 当前状态
    private PaymentStatus currentStatus;

    public PaymentStatus getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(PaymentStatus lastStatus) {
        this.lastStatus = lastStatus;
    }

    public PaymentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(PaymentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }


    /**
     * 根据事件推进状态
     */
    public void transferStatusByEvent(PaymentEvent event) {
        // 根据当前状态和事件，去获取目标状态
        PaymentStatus targetStatus = PaymentStatus.getTargetStatus(currentStatus, event);
        // 如果目标状态不为空，说明是可以推进的
        if (targetStatus != null) {
            lastStatus = currentStatus;
            currentStatus = targetStatus;
        } else {
            // 目标状态为空，说明是非法推进，进入异常处理，这里只是抛出去，由调用者去具体处理
            throw new RuntimeException(String.join(",", currentStatus.toString(), event.toString(), "状态转换失败"));
        }
    }

    @Override
    public String toString() {
        return "PaymentModel{" +
                "lastStatus=" + lastStatus +
                ", currentStatus=" + currentStatus +
                '}';
    }

    public static void main(String[] args) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setCurrentStatus(PaymentStatus.PAYING);
        System.out.println(paymentModel);
        paymentModel.transferStatusByEvent(PaymentEvent.PAY_SUCCESS);
        System.out.println(paymentModel);
    }
}
