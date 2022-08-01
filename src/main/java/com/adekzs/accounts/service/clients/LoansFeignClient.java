package com.adekzs.accounts.service.clients;

import com.adekzs.accounts.model.Customer;
import com.adekzs.accounts.model.Loans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("loans")
public interface LoansFeignClient {

    @RequestMapping(value = "myLoans", method = RequestMethod.POST, consumes = "application/json")
    List<Loans> getLoanDetails(@RequestHeader("adekzs-correlation-id") String correlationId, @RequestBody Customer customer);
}
