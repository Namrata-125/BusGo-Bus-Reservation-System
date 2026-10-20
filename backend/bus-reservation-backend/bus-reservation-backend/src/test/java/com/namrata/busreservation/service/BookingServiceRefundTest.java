package com.namrata.busreservation.service;

import com.namrata.busreservation.entity.Booking;
import com.namrata.busreservation.entity.Payment;
import com.namrata.busreservation.entity.Seat;
import com.namrata.busreservation.entity.User;
import com.namrata.busreservation.repository.BookingRepository;
import com.namrata.busreservation.repository.PaymentRepository;
import com.namrata.busreservation.repository.RouteRepository;
import com.namrata.busreservation.repository.SeatRepository;
import com.namrata.busreservation.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceRefundTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Seat testSeat;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(10L);
        testUser.setEmail("traveler@example.com");

        testSeat = new Seat();
        testSeat.setSeatId(20L);
        testSeat.setSeatNumber("12A");
        testSeat.setSeatStatus(Seat.SeatStatus.BOOKED);

        testBooking = new Booking();
        testBooking.setBookingId(100L);
        testBooking.setUser(testUser);
        testBooking.setSeat(testSeat);
        testBooking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        testBooking.setTotalAmount(new BigDecimal("500.00"));
    }

    @Test
    void cancelMyBooking_whenPaid_shouldCancelBooking_releaseSeat_andRefundPayment() {
        // Arrange
        Payment payment = new Payment();
        payment.setPaymentId(500L);
        payment.setBooking(testBooking);
        payment.setAmount(testBooking.getTotalAmount());
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);

        when(userRepository.findByEmail("traveler@example.com"))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(100L))
                .thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBookingBookingId(100L))
                .thenReturn(List.of(payment));
        when(seatRepository.save(any(Seat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.cancelMyBooking(100L, "traveler@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(Booking.BookingStatus.CANCELLED, result.getBookingStatus(),
                "Booking status should be set to CANCELLED");
        assertEquals(Seat.SeatStatus.AVAILABLE, testSeat.getSeatStatus(),
                "Seat status should be released to AVAILABLE");
        assertEquals(Payment.PaymentStatus.REFUNDED, payment.getPaymentStatus(),
                "Successful payment should be marked as REFUNDED");

        verify(seatRepository, times(1)).save(testSeat);
        verify(paymentRepository, times(1)).save(payment);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void cancelMyBooking_whenUnpaid_shouldCancelBooking_releaseSeat_andNotRefund() {
        // Arrange (no payments exist for this booking)
        when(userRepository.findByEmail("traveler@example.com"))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(100L))
                .thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBookingBookingId(100L))
                .thenReturn(Collections.emptyList());
        when(seatRepository.save(any(Seat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.cancelMyBooking(100L, "traveler@example.com");

        // Assert
        assertEquals(Booking.BookingStatus.CANCELLED, result.getBookingStatus());
        assertEquals(Seat.SeatStatus.AVAILABLE, testSeat.getSeatStatus());

        verify(seatRepository, times(1)).save(testSeat);
        verify(bookingRepository, times(1)).save(testBooking);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void cancelMyBooking_whenPaymentFailedOrPending_shouldNotRefund() {
        // Arrange (payment was not SUCCESS)
        Payment failedPayment = new Payment();
        failedPayment.setPaymentId(501L);
        failedPayment.setBooking(testBooking);
        failedPayment.setPaymentStatus(Payment.PaymentStatus.FAILED);

        Payment pendingPayment = new Payment();
        pendingPayment.setPaymentId(502L);
        pendingPayment.setBooking(testBooking);
        pendingPayment.setPaymentStatus(Payment.PaymentStatus.PENDING);

        when(userRepository.findByEmail("traveler@example.com"))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(100L))
                .thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBookingBookingId(100L))
                .thenReturn(List.of(failedPayment, pendingPayment));
        when(seatRepository.save(any(Seat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.cancelMyBooking(100L, "traveler@example.com");

        // Assert
        assertEquals(Booking.BookingStatus.CANCELLED, result.getBookingStatus());
        assertEquals(Seat.SeatStatus.AVAILABLE, testSeat.getSeatStatus());
        assertEquals(Payment.PaymentStatus.FAILED, failedPayment.getPaymentStatus());
        assertEquals(Payment.PaymentStatus.PENDING, pendingPayment.getPaymentStatus());

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void cancelMyBooking_whenAlreadyCancelled_shouldThrowException() {
        // Arrange
        testBooking.setBookingStatus(Booking.BookingStatus.CANCELLED);

        when(userRepository.findByEmail("traveler@example.com"))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(100L))
                .thenReturn(Optional.of(testBooking));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookingService.cancelMyBooking(100L, "traveler@example.com"));

        assertEquals("Booking is already cancelled", exception.getMessage());
        verify(seatRepository, never()).save(any(Seat.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void cancelBookingAsAdmin_whenPaid_shouldCancelBooking_releaseSeat_andRefundPayment() {
        // Arrange
        Payment payment = new Payment();
        payment.setPaymentId(503L);
        payment.setBooking(testBooking);
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);

        when(bookingRepository.findById(100L))
                .thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBookingBookingId(100L))
                .thenReturn(List.of(payment));
        when(seatRepository.save(any(Seat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.cancelBookingAsAdmin(100L);

        // Assert
        assertEquals(Booking.BookingStatus.CANCELLED, result.getBookingStatus());
        assertEquals(Seat.SeatStatus.AVAILABLE, testSeat.getSeatStatus());
        assertEquals(Payment.PaymentStatus.REFUNDED, payment.getPaymentStatus());

        verify(seatRepository, times(1)).save(testSeat);
        verify(paymentRepository, times(1)).save(payment);
        verify(bookingRepository, times(1)).save(testBooking);
    }
}
