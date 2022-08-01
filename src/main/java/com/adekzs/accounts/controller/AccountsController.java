package com.adekzs.accounts.controller;


import com.adekzs.accounts.config.AccountServiceConfig;
import com.adekzs.accounts.model.*;
import com.adekzs.accounts.repository.AccountsRepository;
import com.adekzs.accounts.service.clients.CardsFeignClient;
import com.adekzs.accounts.service.clients.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.netflix.discovery.converters.Auto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class AccountsController {


    public static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountServiceConfig accountServiceConfig;

    @Autowired
    private CardsFeignClient cardsFeignClient;

    @Autowired
    private LoansFeignClient loansFeignClient;

    @PostMapping("/myAccount")
    @Timed(value = "getAccountDetails.time", description = "Time taken to return account details")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        System.out.println("Fell in getAccounts");
        return accounts;
    }

    @GetMapping("/accounts/properties")
    public String getPropertiesDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer()
                .withDefaultPrettyPrinter();
        Properties properties = new Properties(accountServiceConfig.getMsg(),
                accountServiceConfig.getBuildVersion(), accountServiceConfig.getMailDetails(), accountServiceConfig.getActiveBranches());
        return ow.writeValueAsString(properties);

    }

    @PostMapping("/myCustomerDetails")
    @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallback")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallback")
    public CustomerDetails myCustomerDetails(@RequestHeader("adekzs-correlation-id") String correlationId,  @RequestBody Customer customer) {
        logger.info("myCustomerDetails() method started");
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoanDetails(correlationId, customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(correlationId, customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setCards(cards);
        customerDetails.setLoans(loans);
        logger.info("myCustomerDetails() method ended ");
        return  customerDetails;
    }


    private CustomerDetails myCustomerDetailsFallback(String correlationId, Customer customer, Throwable t) {

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoanDetails(correlationId, customer);


        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);

        return  customerDetails;
    }

    @GetMapping("/sayhello")
    @RateLimiter(name = "sayhello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        return "Hello, Welcome to adekzsBank";
    }

    private String sayHelloFallback(Throwable t) {
        return "Welcome to adekzsBank";
    }
}
