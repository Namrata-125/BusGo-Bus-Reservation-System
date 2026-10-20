package com.namrata.busreservation.service;

import com.namrata.busreservation.entity.Booking;
import com.namrata.busreservation.entity.Payment;
import com.namrata.busreservation.entity.Route;
import com.namrata.busreservation.entity.Seat;
import com.namrata.busreservation.entity.User;

import com.namrata.busreservation.repository.BookingRepository;
import com.namrata.busreservation.repository.PaymentRepository;
import com.namrata.busreservation.repository.RouteRepository;
import com.namrata.busreservation.repository.SeatRepository;
import com.namrata.busreservation.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final PaymentRepository paymentRepository;

    public BookingService(
            BookingRepository bookingRepository,
            SeatRepository seatRepository,
            UserRepository userRepository,
            RouteRepository routeRepository,
            PaymentRepository paymentRepository) {

        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Booking createBooking(
            Booking booking,
            String email) {

        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Find requested route
        Route route = routeRepository.findById(
                booking.getRoute().getRouteId()
        ).orElseThrow(() ->
                new RuntimeException("Route not found"));

        // Find requested seat
        Seat seat = seatRepository.findById(
                booking.getSeat().getSeatId()
        ).orElseThrow(() ->
                new RuntimeException("Seat not found"));

        // Check whether seat belongs to the route's bus
        if (!seat.getBus().getBusId()
                .equals(route.getBus().getBusId())) {

            throw new RuntimeException(
                    "Selected seat does not belong to this route's bus"
            );
        }

        // Check whether seat is already booked
        if (seat.getSeatStatus() == Seat.SeatStatus.BOOKED) {

            throw new RuntimeException(
                    "Seat is already booked"
            );
        }

        // Mark seat as booked
        seat.setSeatStatus(
                Seat.SeatStatus.BOOKED
        );

        seatRepository.save(seat);

        // Set server-side booking information
        booking.setUser(user);
        booking.setRoute(route);
        booking.setSeat(seat);

        booking.setBookingDate(
                LocalDateTime.now()
        );

        booking.setBookingStatus(
                Booking.BookingStatus.CONFIRMED
        );

        // Set fare from the route stored in the database
        booking.setTotalAmount(
                route.getFare()
        );

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(
            Long bookingId) {

        return bookingRepository.findById(
                bookingId
        );
    }

    public List<Booking> getMyBookings(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return bookingRepository.findByUserUserId(
                user.getUserId()
        );
    }

    public Booking getMyBookingById(
            Long bookingId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(
                bookingId
        ).orElseThrow(() ->
                new RuntimeException("Booking not found"));

        if (!booking.getUser().getUserId()
                .equals(user.getUserId())) {

            throw new RuntimeException(
                    "You are not authorized to view this booking"
            );
        }

        return booking;
    }

    public List<Booking> getBookingsByUser(
            Long userId) {

        return bookingRepository.findByUserUserId(
                userId
        );
    }

    public List<Booking> getBookingsByRoute(
            Long routeId) {

        return bookingRepository.findByRouteRouteId(
                routeId
        );
    }

    @Transactional
    public Booking cancelMyBooking(
            Long bookingId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(
                bookingId
        ).orElseThrow(() ->
                new RuntimeException("Booking not found"));

        if (!booking.getUser().getUserId()
                .equals(user.getUserId())) {

            throw new RuntimeException(
                    "You are not authorized to cancel this booking"
            );
        }

        return cancelBookingInternal(booking);
    }

    @Transactional
    public Booking cancelBookingAsAdmin(
            Long bookingId) {

        Booking booking = bookingRepository.findById(
                bookingId
        ).orElseThrow(() ->
                new RuntimeException("Booking not found"));

        return cancelBookingInternal(booking);
    }

    private Booking cancelBookingInternal(
            Booking booking) {

        if (booking.getBookingStatus()
                == Booking.BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Booking is already cancelled"
            );
        }

        booking.setBookingStatus(
                Booking.BookingStatus.CANCELLED
        );

        Seat seat = booking.getSeat();

        seat.setSeatStatus(
                Seat.SeatStatus.AVAILABLE
        );

        seatRepository.save(seat);

        // Simulated refund: Mark any associated SUCCESS payment as REFUNDED
        List<Payment> payments = paymentRepository.findByBookingBookingId(booking.getBookingId());
        for (Payment payment : payments) {
            if (payment.getPaymentStatus() == Payment.PaymentStatus.SUCCESS) {
                payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        }

        return bookingRepository.save(booking);
    }
}