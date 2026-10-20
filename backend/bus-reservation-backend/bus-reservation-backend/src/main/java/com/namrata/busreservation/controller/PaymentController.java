package com.namrata.busreservation.controller;

import com.namrata.busreservation.entity.Payment;
import com.namrata.busreservation.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> processPayment(
            @RequestBody Payment payment) {

        try {

            Payment savedPayment =
                    paymentService.processPayment(payment);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedPayment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(
            @PathVariable Long paymentId) {

        return paymentService
                .getPaymentById(paymentId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null)
                );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByBooking(
                        bookingId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<?> getPaymentByTransactionId(
            @PathVariable String transactionId) {

        return paymentService
                .getPaymentByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null)
                );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(
            @PathVariable Payment.PaymentStatus status) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByStatus(status)
        );
    }
}