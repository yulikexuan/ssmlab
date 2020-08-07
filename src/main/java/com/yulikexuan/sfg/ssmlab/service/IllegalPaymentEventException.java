//: com.yulikexuan.sfg.ssmlab.service.IllegalPaymentEventException.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.domain.PaymentEvent;


public class IllegalPaymentEventException extends IllegalArgumentException {

    private final PaymentEvent illegalPaymentEvent;

    public IllegalPaymentEventException(PaymentEvent illegalPaymentEvent) {
        super();
        this.illegalPaymentEvent = illegalPaymentEvent;
    }

    public IllegalPaymentEventException(String s, PaymentEvent illegalPaymentEvent) {
        super(s);
        this.illegalPaymentEvent = illegalPaymentEvent;
    }

}///:~