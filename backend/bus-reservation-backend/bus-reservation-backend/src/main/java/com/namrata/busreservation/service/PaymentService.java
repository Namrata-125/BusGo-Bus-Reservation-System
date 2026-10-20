package com.namrata.busreservation.service;

import com.namrata.busreservation.entity.Booking;
import com.namrata.busreservation.entity.Payment;
import com.namrata.busreservation.repository.BookingRepository;
import com.namrata.busreservation.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Payment processPayment(Payment payment) {

        // Find the booking
        Booking booking = bookingRepository.findById(
                payment.getBooking().getBookingId()
        ).orElseThrow(() ->
                new RuntimeException("Booking not found"));

        // Do not allow payment for cancelled booking
        if (booking.getBookingStatus()
                == Booking.BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cannot make payment for a cancelled booking"
            );
        }

        // Prevent duplicate successful payment
        if (paymentRepository
                .existsByBookingBookingIdAndPaymentStatus(
                        booking.getBookingId(),
                        Payment.PaymentStatus.SUCCESS)) {

            throw new RuntimeException(
                    "Payment already completed for this booking"
            );
        }

        // Connect payment with the actual booking
        payment.setBooking(booking);

        // Always use the booking amount
        payment.setAmount(
                booking.getTotalAmount()
        );

        // Set payment date
        payment.setPaymentDate(
                LocalDateTime.now()
        );

        // Simulated successful payment
        payment.setPaymentStatus(
                Payment.PaymentStatus.SUCCESS
        );

        // Generate transaction ID
        payment.setTransactionId(
                "TXN-" + UUID.randomUUID()
        );

        // Save payment
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(
            Long paymentId) {

        return paymentRepository.findById(paymentId);
    }

    public List<Payment> getPaymentsByBooking(
            Long bookingId) {

        return paymentRepository.findByBookingBookingId(
                bookingId
        );
    }

    public Optional<Payment> getPaymentByTransactionId(
            String transactionId) {

        return paymentRepository.findByTransactionId(
                transactionId
        );
    }

    public List<Payment> getPaymentsByStatus(
            Payment.PaymentStatus status) {

        return paymentRepository.findByPaymentStatus(
                status
        );
    }
}