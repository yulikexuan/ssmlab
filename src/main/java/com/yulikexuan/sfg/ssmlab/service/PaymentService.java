//: com.yulikexuan.sfg.ssmlab.service.PaymentService.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.domain.Payment;
import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.access.StateMachineAccessor;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
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

    private StateMachine<PaymentState, PaymentEvent> buildAndRehydrate(UUID paymentId) {

        Payment payment = this.paymentRepository.getOne(paymentId);

        StateMachine<PaymentState, PaymentEvent> stateMachine =
                this.stateMachineFactory.getStateMachine(payment.getId());

        stateMachine.stop();

        /*
         * StateMachine provides an APIs for generic finite state machine
         * needed for basic operations like working with states, events and a
         * lifecycle
         */
        StateMachineAccessor<PaymentState, PaymentEvent> stateMachineAccessor =
                stateMachine.getStateMachineAccessor();

        /*
         * Execute given StateMachineFunction with all recursive regions
         *
         * A region is an orthogonal part of either a composite state or a
         * state machine
         *   - It contains states and transition
         *
         * StateMachineContext represents a current state of a state machine
         */
        stateMachineAccessor.doWithAllRegions(
                stateMachineAccess -> stateMachineAccess.resetStateMachine(
                        new DefaultStateMachineContext<>(payment.getState(),
                                null, null, null)));

        stateMachine.start();

        return stateMachine;
    }

}///:~