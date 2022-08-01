package com.adekzs.accounts.service.clients;

import com.adekzs.accounts.model.Cards;
import com.adekzs.accounts.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("cards")
public interface CardsFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "myCards", consumes = "application/json")
    List<Cards> getCardDetails(@RequestHeader("adekzs-correlation-id") String correlationId, @RequestBody Customer customer);

}
