package com.example.payments.controller;

import com.example.payments.dto.BankChargeRequest;
import com.example.payments.dto.BankChargeResponse;
import com.example.payments.dto.PaymentCreateResponse;
import com.example.payments.exception.PaymentFailedException;
import com.example.payments.exception.PaymentStatusInvalidException;
import com.example.payments.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private PaymentService paymentService;

    public PaymentsController(PaymentService createPayment) {
        this.paymentService = createPayment;
    }

    @PostMapping
    public PaymentCreateResponse create(BankChargeRequest dto)
            throws PaymentStatusInvalidException, PaymentFailedException {
        BankChargeResponse bankChargeResponse = paymentService.createPayment(dto);
        return new PaymentCreateResponse(bankChargeResponse.id());
    }
}
