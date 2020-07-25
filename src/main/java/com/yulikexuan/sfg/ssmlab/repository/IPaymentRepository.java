//: com.yulikexuan.sfg.ssmlab.repository.IPaymentRepository.java


package com.yulikexuan.sfg.ssmlab.repository;


import com.yulikexuan.sfg.ssmlab.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface IPaymentRepository extends JpaRepository<Payment, UUID> {

}///:~