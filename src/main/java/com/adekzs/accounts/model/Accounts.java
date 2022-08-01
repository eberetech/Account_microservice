package com.adekzs.accounts.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode
public class Accounts {

    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "account_number")
    @Id
    private long accountNumber;
    @Column(name = "account_type")
    private String accountType;
    @Column(name = "branch_address")
    private String branchAddress;
    @Column(name = "createDt")
    private LocalDate createDt;

}
