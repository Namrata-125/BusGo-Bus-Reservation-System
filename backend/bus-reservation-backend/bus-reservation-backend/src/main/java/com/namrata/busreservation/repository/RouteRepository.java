package com.namrata.busreservation.repository;

import com.namrata.busreservation.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThan(
            String source,
            String destination,
            LocalDateTime start,
            LocalDateTime end
    );
}