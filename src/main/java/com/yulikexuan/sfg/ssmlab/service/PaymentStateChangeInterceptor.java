//: com.yulikexuan.sfg.ssmlab.service.PaymentStateChangeListener.java


package com.yulikexuan.sfg.ssmlab.service;

import com.yulikexuan.sfg.ssmlab.domain.Payment;
import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends
        StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final IPaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine) {

        Optional.ofNullable(message)
                .map(this::getPaymentId)
                .map(paymentRepository::getOne)
                .ifPresent(payment -> {
                    payment.setState(state.getId());
                    paymentRepository.save(payment);
                });
    }

    private UUID getPaymentId(final Message<PaymentEvent> msg) {
        return (UUID) msg.getHeaders().get(PaymentService.PAYMENT_ID_MSG_HEADER);
    }

}///:~