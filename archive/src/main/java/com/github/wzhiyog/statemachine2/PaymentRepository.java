package com.github.wzhiyog.statemachine2;

import org.springframework.stereotype.Component;

@Component
public class PaymentRepository {
    public Payment findByIdWithLock(String paymentId) {
        return null;
    }

    public Payment save(Payment payment) {

        return payment;
    }
}
