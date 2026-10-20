package com.namrata.busreservation.controller;

import com.namrata.busreservation.entity.Booking;
import com.namrata.busreservation.service.BookingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService) {

        this.bookingService = bookingService;
    }

    // CREATE BOOKING
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody Booking booking,
            Authentication authentication) {

        try {

            String email =
                    authentication.getName();

            Booking savedBooking =
                    bookingService.createBooking(
                            booking,
                            email
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBooking);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // GET ALL BOOKINGS - ADMIN
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings()
        );
    }

    // GET MY BOOKINGS - USER
    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(
            Authentication authentication) {

        try {

            String email =
                    authentication.getName();

            return ResponseEntity.ok(
                    bookingService.getMyBookings(
                            email
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // GET MY BOOKING BY ID - USER
    @GetMapping("/my/{bookingId}")
    public ResponseEntity<?> getMyBookingById(
            @PathVariable Long bookingId,
            Authentication authentication) {

        try {

            String email =
                    authentication.getName();

            Booking booking =
                    bookingService.getMyBookingById(
                            bookingId,
                            email
                    );

            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    // GET BOOKING BY ID - ADMIN
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(
            @PathVariable Long bookingId) {

        return bookingService
                .getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null)
                );
    }

    // GET BOOKINGS BY USER - ADMIN
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByUser(
                        userId
                )
        );
    }

    // GET BOOKINGS BY ROUTE - ADMIN
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<Booking>> getBookingsByRoute(
            @PathVariable Long routeId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByRoute(
                        routeId
                )
        );
    }

    // CANCEL OWN BOOKING - USER
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelMyBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        try {

            String email =
                    authentication.getName();

            Booking cancelledBooking =
                    bookingService.cancelMyBooking(
                            bookingId,
                            email
                    );

            return ResponseEntity.ok(
                    cancelledBooking
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    // CANCEL ANY BOOKING - ADMIN
    @PutMapping("/admin/{bookingId}/cancel")
    public ResponseEntity<?> cancelBookingAsAdmin(
            @PathVariable Long bookingId) {

        try {

            Booking cancelledBooking =
                    bookingService.cancelBookingAsAdmin(
                            bookingId
                    );

            return ResponseEntity.ok(
                    cancelledBooking
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}