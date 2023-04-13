package com.example.payments.dto;

public record BankChargeResponse(String id, PaymentStatus status, String message) {
}
