//: com.yulikexuan.sfg.ssmlab.service.PaymentService.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.domain.Payment;
import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineAccessor;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
final class PaymentService implements IPaymentService {

    static final String PAYMENT_ID_MSG_HEADER = "payment_ic";

    private final IPaymentRepository paymentRepository;

    private final StateMachineFactory<PaymentState, PaymentEvent>
            stateMachineFactory;

    private final StateMachineInterceptorAdapter<PaymentState, PaymentEvent>
            paymentStateMachineInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return this.paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(UUID paymentId) {
        return Optional.ofNullable(paymentId)
                .flatMap(this.paymentRepository::findById);
    }

    @Override
    public Payment savePayment(Payment payment) {
        return this.paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(UUID paymentId) {

        StateMachine<PaymentState, PaymentEvent> stateMachine =
                this.buildStateMachineWithPayment(paymentId);

        this.sendPaymentEventToStateMachine(paymentId, stateMachine,
                PaymentEvent.PRE_AUTHORIZE);

        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(UUID paymentId) {

        StateMachine<PaymentState, PaymentEvent> stateMachine =
                this.buildStateMachineWithPayment(paymentId);

        this.sendPaymentEventToStateMachine(paymentId, stateMachine,
                PaymentEvent.AUTH_APPROVED);

        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(UUID paymentId) {

        StateMachine<PaymentState, PaymentEvent> stateMachine =
                this.buildStateMachineWithPayment(paymentId);

        this.sendPaymentEventToStateMachine(paymentId, stateMachine,
                PaymentEvent.AUTH_DECLINED);

        return null;
    }

    private StateMachine<PaymentState, PaymentEvent> buildStateMachineWithPayment(
            UUID paymentId) {

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
                stateMachineAccess -> {
                    stateMachineAccess.addStateMachineInterceptor(
                            this.paymentStateMachineInterceptor);
                    stateMachineAccess.resetStateMachine(
                        new DefaultStateMachineContext<>(payment.getState(),
                                null, null, null));
                });

        stateMachine.start();

        return stateMachine;
    }

    private void sendPaymentEventToStateMachine(final UUID paymentId,
            final StateMachine<PaymentState, PaymentEvent> stateMachine,
            final PaymentEvent paymentEvent) {

        Message<PaymentEvent> paymentMessage =
                MessageBuilder.withPayload(paymentEvent)
                        .setHeader(PAYMENT_ID_MSG_HEADER, paymentId)
                        .build();

        stateMachine.sendEvent(paymentMessage);
    }

}///:~