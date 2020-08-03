//: com.yulikexuan.sfg.ssmlab.service.IPaymentService.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.domain.Payment;
import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

import java.util.Optional;
import java.util.UUID;


public interface IPaymentService {

    Payment newPayment(Payment payment);

    Optional<Payment> getPaymentById(UUID paymentId);

    Payment savePayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(UUID paymentId);

    StateMachine<PaymentState, PaymentEvent> authorizePayment(UUID paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuth(UUID paymentId);

}///:~