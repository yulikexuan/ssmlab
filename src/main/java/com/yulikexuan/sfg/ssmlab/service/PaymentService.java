//: com.yulikexuan.sfg.ssmlab.service.PaymentService.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.domain.Payment;
import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;



@Slf4j
@RequiredArgsConstructor
final class PaymentService implements IPaymentService {

    private final IPaymentRepository paymentRepository;

    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return this.paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(UUID paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(UUID paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(UUID paymentId) {
        return null;
    }

}///:~