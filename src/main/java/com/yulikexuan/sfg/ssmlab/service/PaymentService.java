//: com.yulikexuan.sfg.ssmlab.service.PaymentService.java


package com.yulikexuan.sfg.ssmlab.service;


import com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final IPaymentRepository paymentRepository;

}///:~