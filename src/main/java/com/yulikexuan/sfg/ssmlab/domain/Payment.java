//: com.yulikexuan.sfg.ssmlab.domain.Payment.java


package com.yulikexuan.sfg.ssmlab.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Entity
@NoArgsConstructor
@Builder @AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false )
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    private BigDecimal amount;

}///:~