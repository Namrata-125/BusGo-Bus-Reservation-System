package com.namrata.busreservation.controller;

import com.namrata.busreservation.entity.Bus;
import com.namrata.busreservation.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@CrossOrigin(origins = "*")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    // Add a new bus
    @PostMapping
    public ResponseEntity<?> addBus(@RequestBody Bus bus) {

        try {
            Bus savedBus = busService.addBus(bus);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBus);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Get all buses
    @GetMapping
    public ResponseEntity<List<Bus>> getAllBuses() {

        return ResponseEntity.ok(
                busService.getAllBuses()
        );
    }

    // Get bus by ID
    @GetMapping("/{busId}")
    public ResponseEntity<?> getBusById(
            @PathVariable Long busId) {

        return busService.getBusById(busId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null)
                );
    }

    // Update bus
    @PutMapping("/{busId}")
    public ResponseEntity<?> updateBus(
            @PathVariable Long busId,
            @RequestBody Bus updatedBus) {

        try {

            Bus bus = busService.updateBus(
                    busId,
                    updatedBus
            );

            return ResponseEntity.ok(bus);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Delete bus
    @DeleteMapping("/{busId}")
    public ResponseEntity<?> deleteBus(
            @PathVariable Long busId) {

        try {

            busService.deleteBus(busId);

            return ResponseEntity.ok(
                    "Bus deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
