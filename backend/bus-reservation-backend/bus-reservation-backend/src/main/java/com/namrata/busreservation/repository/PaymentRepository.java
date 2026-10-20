package com.namrata.busreservation.repository;

import com.namrata.busreservation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByBookingBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByPaymentStatus(
            Payment.PaymentStatus paymentStatus);

    boolean existsByBookingBookingIdAndPaymentStatus(
            Long bookingId,
            Payment.PaymentStatus paymentStatus);
}