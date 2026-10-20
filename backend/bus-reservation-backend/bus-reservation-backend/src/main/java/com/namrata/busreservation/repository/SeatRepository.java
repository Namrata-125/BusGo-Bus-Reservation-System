package com.namrata.busreservation.repository;

import com.namrata.busreservation.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusBusId(Long busId);

    List<Seat> findByBusBusIdAndSeatStatus(
            Long busId,
            Seat.SeatStatus seatStatus
    );
}
