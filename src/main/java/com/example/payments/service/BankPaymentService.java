package com.example.payments.service;

import com.example.payments.dto.BankChargeResponse;
import com.example.payments.dto.BankChargeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BankPaymentService {

    @Value("${bank.url}")
    private String bankUrl;

    public BankChargeResponse sendPaymentToBank(BankChargeRequest dto){
        WebClient webClient = WebClient.builder().baseUrl(bankUrl).build();
        return webClient
                .post()
                .uri("/charge")
                .body(BodyInserters.fromValue(dto))
                .retrieve()
                .bodyToMono(BankChargeResponse.class)
                .block();
    }
}
