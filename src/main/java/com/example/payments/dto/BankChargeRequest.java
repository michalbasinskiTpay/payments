package com.example.payments.dto;

public record BankChargeRequest(Integer amount, String cardNumber, String cvc){
}