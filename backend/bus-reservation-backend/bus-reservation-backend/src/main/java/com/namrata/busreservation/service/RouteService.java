package com.namrata.busreservation.service;

import com.namrata.busreservation.entity.Route;
import com.namrata.busreservation.repository.RouteRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public Route addRoute(Route route) {
        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRouteById(Long routeId) {
        return routeRepository.findById(routeId);
    }

    public Route updateRoute(Long routeId, Route updatedRoute) {

        Route existingRoute = routeRepository.findById(routeId)
                .orElseThrow(() ->
                        new RuntimeException("Route not found"));

        existingRoute.setBus(updatedRoute.getBus());
        existingRoute.setSource(updatedRoute.getSource());
        existingRoute.setDestination(updatedRoute.getDestination());
        existingRoute.setDepartureTime(updatedRoute.getDepartureTime());
        existingRoute.setArrivalTime(updatedRoute.getArrivalTime());
        existingRoute.setFare(updatedRoute.getFare());

        return routeRepository.save(existingRoute);
    }

    public void deleteRoute(Long routeId) {

        if (!routeRepository.existsById(routeId)) {
            throw new RuntimeException("Route not found");
        }

        routeRepository.deleteById(routeId);
    }

    // SEARCH ROUTES
    public List<Route> searchRoutes(
            String source,
            String destination,
            LocalDate date) {

        LocalDateTime startOfDay =
                date.atStartOfDay();

        LocalDateTime startOfNextDay =
                date.plusDays(1).atStartOfDay();

        return routeRepository
                .findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThan(
                        source,
                        destination,
                        startOfDay,
                        startOfNextDay
                );
    }
}