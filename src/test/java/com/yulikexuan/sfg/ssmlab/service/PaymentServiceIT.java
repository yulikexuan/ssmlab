//: com.yulikexuan.sfg.ssmlab.service.PaymentServiceIT.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.domain.Payment;
import com.yulikexuan.sfg.ssmlab.domain.PaymentState;
import com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@DisplayName("State Machine Service Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentServiceIT {

    private Payment payment;

    @Autowired
    private IPaymentRepository paymentRepository;

    @Autowired
    private IPaymentService paymentService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @Transactional
    void test_Given_A_New_Payment_When_Pre_Approved_Auth_Then_Being_Pre_Auth_State() {

        // Given
        this.payment = Payment.builder()
                .amount(new BigDecimal("12.99"))
                .build();

        Payment givenPayment = this.paymentService.newPayment(this.payment);

        // When
        this.paymentService.preAuth(givenPayment.getId());
        Payment preAuthorizedPayment = this.paymentRepository.getOne(
                givenPayment.getId());

        // Then
        log.debug(">>>>>>> The pre-authorized payment is {}", preAuthorizedPayment);
        assertThat(preAuthorizedPayment.getState()).isIn(PaymentState.PRE_AUTH,
                PaymentState.PRE_AUTH_ERROR);
    }

    @Test
    @RepeatedTest(3)
    @Transactional
    void test_Given_A_Pre_Authorized_Payment_When_Approved_Auth_Then_Being_Pre_Auth_State() {

        // Given
        this.payment = Payment.builder()
                .amount(new BigDecimal("12.99"))
                .state(PaymentState.PRE_AUTH)
                .build();

        Payment givenPayment = this.paymentService.savePayment(this.payment);

        // When
        this.paymentService.authorizePayment(givenPayment.getId());
        Payment authorizedPayment = this.paymentRepository.getOne(
                givenPayment.getId());

        // Then
        log.debug(">>>>>>> The authorized payment is {}", authorizedPayment);
        assertThat(authorizedPayment.getState()).isIn(PaymentState.AUTH,
                PaymentState.AUTH_ERROR);
    }

}///:~