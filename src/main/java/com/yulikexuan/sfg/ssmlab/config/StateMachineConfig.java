//: com.yulikexuan.sfg.ssmlab.config.StateMachineConfig.java


package com.yulikexuan.sfg.ssmlab.config;


import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import com.yulikexuan.sfg.ssmlab.service.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends
        StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    static ThreadLocalRandom random = ThreadLocalRandom.current();

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states)
            throws Exception {

        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    /*
     * A transition is a relationship between a source state and a target state
     * A switch from a state to another is a state transition caused by a trigger
     *
     * Internal Transition
     *   - Internal transition is used when action needs to be executed without
     *     causing a state transition
     *   - With internal transition source and target state is always a same and
     *     it is identical with self-transition in the absence of state entry
     *     and exit actions
     *
     * External vs. Local Transition
     *   - Most of the cases external and local transition are functionally
     *     equivalent except in cases where transition is happening between
     *     super and sub states
     *   - Local transition doesn’t cause exit and entry to source state if
     *     target state is a substate of a source state. Other way around,
     *     local transition doesn’t cause exit and entry to target state if
     *     target is a superstate of a source state.
     */
    @Override
    public void configure(
            StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions)
            throws Exception {

        transitions.withExternal()
                .source(PaymentState.NEW)
                .target(PaymentState.NEW)
                .event(PaymentEvent.PRE_AUTHORIZE)
                .action(PRE_AUTH_ACTION)
                .and()
                .withExternal()
                .source(PaymentState.NEW)
                .target(PaymentState.PRE_AUTH)
                .event(PaymentEvent.PRE_AUTH_APPROVED)
                .and()
                .withExternal()
                .source(PaymentState.NEW)
                .target(PaymentState.PRE_AUTH_ERROR)
                .event(PaymentEvent.PRE_AUTH_DECLINED);
    }

    @Override
    public void configure(
            StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config)
            throws Exception {

        config.withConfiguration().listener(
                new StateMachineListenerAdapter<>() {
                    @Override
                    public void stateChanged(
                            State<PaymentState, PaymentEvent> from,
                            State<PaymentState, PaymentEvent> to) {

                        if (Objects.isNull(from) && Objects.isNull(to)) {
                            return;
                        }

                        if (Objects.equals(from, to)) {
                            return;
                        }

                        PaymentState fromState = Objects.isNull(from) ?
                                null : from.getId();

                        PaymentState toState = Objects.isNull(to) ?
                                null : to.getId();

                        log.info(">>>>>>> The state changed from {} to {}",
                                fromState, toState);
                    }
                });
    }

    private static final Action<PaymentState, PaymentEvent> PRE_AUTH_ACTION =
            stateContext -> {
                log.debug(">>>>>>> The {} event was called. ", PaymentEvent.PRE_AUTHORIZE);
                int chance = random.nextInt(0, 10);
                final var msgHeader = stateContext.getMessageHeader(
                        IPaymentService.PAYMENT_ID_MSG_HEADER);
                PaymentEvent successorEvent = (chance < 8) ?
                        PaymentEvent.PRE_AUTH_APPROVED : PaymentEvent.PRE_AUTH_DECLINED;
                log.debug(">>>>>>> Sending successor event: {}", successorEvent);
                stateContext.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(successorEvent)
                                .setHeader(IPaymentService.PAYMENT_ID_MSG_HEADER, msgHeader)
                                .build()
                );
            };

}///:~