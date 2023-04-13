package com.example.payments.service;

import com.example.payments.dto.BankChargeResponse;
import com.example.payments.dto.PaymentStatus;
import com.example.payments.dto.BankChargeRequest;
import com.example.payments.exception.PaymentFailedException;
import com.example.payments.exception.PaymentStatusInvalidException;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final BankPaymentService bankPaymentService;

    public PaymentService(BankPaymentService bankPaymentService) {
        this.bankPaymentService = bankPaymentService;
    }

    public BankChargeResponse createPayment(BankChargeRequest request) throws PaymentStatusInvalidException, PaymentFailedException {
        BankChargeResponse response = bankPaymentService.sendPaymentToBank(request);
        validResponse(response);
        return response;
    }

    private void validResponse(BankChargeResponse response) throws PaymentFailedException, PaymentStatusInvalidException {
        if (response == null) {
            throw new PaymentFailedException();
        }
        if (response.status() == null) {
            throw new PaymentStatusInvalidException();
        }
        if (PaymentStatus.FAILED.equals(response.status())) {
            throw new PaymentFailedException();
        }
    }
}
