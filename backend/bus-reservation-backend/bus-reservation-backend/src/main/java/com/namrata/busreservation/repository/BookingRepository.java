package com.namrata.busreservation.repository;

import com.namrata.busreservation.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUserUserId(Long userId);

    List<Booking> findByRouteRouteId(Long routeId);

    List<Booking> findBySeatSeatId(Long seatId);

    List<Booking> findByBookingStatus(
            Booking.BookingStatus bookingStatus
    );
}
