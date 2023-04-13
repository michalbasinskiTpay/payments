package com.example.payments.service;

import com.example.payments.dto.BankChargeRequest;
import com.example.payments.dto.BankChargeResponse;
import com.example.payments.dto.PaymentStatus;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest(httpPort = 8081)
class BankPaymentServiceTest {

    @Autowired
    public BankPaymentService bankPaymentService;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("bank.url", () -> "http://localhost:8081");
    }

    @Test
    void should_return_correct_payment() throws IOException {
        // given
        String responseBody = IOUtils.resourceToString("/files/bank-payment-service-test/correct-response.json", StandardCharsets.UTF_8);

        stubFor(post(urlEqualTo("/charge"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)
                )
        );

        // when
        BankChargeResponse response = bankPaymentService.sendPaymentToBank(new BankChargeRequest(100, "4790627202424467", "141"));
        // then
        assertAll(() -> {
            assertEquals("1", response.id());
            assertEquals(PaymentStatus.ACCEPTED, response.status());
        });
        verify(exactly(1), postRequestedFor(urlEqualTo("/charge")));
    }

    @Test
    void should_return_failed_payment() throws IOException {
        // given
        String responseBody = IOUtils.resourceToString("/files/bank-payment-service-test/failed-response.json", StandardCharsets.UTF_8);

        stubFor(post(urlEqualTo("/charge"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)
                )
        );

        // when
        BankChargeResponse response = bankPaymentService.sendPaymentToBank(new BankChargeRequest(100, "4790627202424467", ""));

        // then
        assertAll(() -> {
            assertNull(response.id());
            assertEquals(PaymentStatus.FAILED, response.status());
            assertNotNull(response.message());
        });
        verify(exactly(1), postRequestedFor(urlEqualTo("/charge")));
    }

    @Test
    void should_return_exception() {
        // given
        stubFor(post(urlEqualTo("/charge"))
                .willReturn(
                        aResponse()
                                .withStatus(500)
                )
        );

        // when // then
        assertThrows(Exception.class, () -> {
            bankPaymentService.sendPaymentToBank(new BankChargeRequest(100, "4790627213724467", ""));
        });
    }

    @Test
    void should_return_correct_payment_delayed() throws IOException {
        // given
        String responseBody = IOUtils.resourceToString("/files/bank-payment-service-test/correct-response.json", StandardCharsets.UTF_8);

        stubFor(post(urlEqualTo("/charge"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseBody)
                                .withFault(Fault.CONNECTION_RESET_BY_PEER)
                )
        );

        // when // then
        assertThrows(Exception.class, () -> {
            bankPaymentService.sendPaymentToBank(new BankChargeRequest(100, "4790627213724467", ""));
        });
    }
}