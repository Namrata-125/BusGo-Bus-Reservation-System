package com.namrata.busreservation.controller;

import com.namrata.busreservation.entity.Route;
import com.namrata.busreservation.service.RouteService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<?> addRoute(
            @RequestBody Route route) {

        try {

            Route savedRoute =
                    routeService.addRoute(route);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedRoute);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {

        return ResponseEntity.ok(
                routeService.getAllRoutes()
        );
    }

    // SEARCH ROUTES
    @GetMapping("/search")
    public ResponseEntity<List<Route>> searchRoutes(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                routeService.searchRoutes(
                        source,
                        destination,
                        date
                )
        );
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<?> getRouteById(
            @PathVariable Long routeId) {

        return routeService
                .getRouteById(routeId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null)
                );
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<?> updateRoute(
            @PathVariable Long routeId,
            @RequestBody Route updatedRoute) {

        try {

            Route route =
                    routeService.updateRoute(
                            routeId,
                            updatedRoute
                    );

            return ResponseEntity.ok(route);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(
            @PathVariable Long routeId) {

        try {

            routeService.deleteRoute(routeId);

            return ResponseEntity.ok(
                    "Route deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}