//: com.yulikexuan.sfg.ssmlab.config.StateMachineConfigIT.java


package com.yulikexuan.sfg.ssmlab.config;


import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@DisplayName("Test State Machine Configuration - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StateMachineConfigIT {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    private StateMachine<PaymentState, PaymentEvent> stateMachine;

    @BeforeEach
    void setUp() {
        this.stateMachine = this.stateMachineFactory.getStateMachine(
                UUID.randomUUID());
    }

    @Test
    void test_Payment_State_Machine_Transitions() {

        // Given
        this.stateMachine.start();

        // When
        PaymentState initialState = this.stateMachine.getState().getId();

        this.stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);

        PaymentState startState = this.stateMachine.getState().getId();

        this.stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);

        PaymentState finalState = this.stateMachine.getState().getId();

        // Then
        assertThat(initialState).isEqualTo(PaymentState.NEW);
        assertThat(startState).isEqualTo(PaymentState.NEW);
        assertThat(finalState).isEqualTo(PaymentState.PRE_AUTH);
    }

}///:~