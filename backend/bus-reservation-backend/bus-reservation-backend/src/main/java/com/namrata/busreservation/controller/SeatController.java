package com.namrata.busreservation.controller;

import com.namrata.busreservation.entity.Seat;
import com.namrata.busreservation.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "*")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<?> addSeat(@RequestBody Seat seat) {
        try {
            Seat savedSeat = seatService.addSeat(seat);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedSeat);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        return ResponseEntity.ok(
                seatService.getAllSeats()
        );
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<?> getSeatById(
            @PathVariable Long seatId) {

        return seatService.getSeatById(seatId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null)
                );
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<Seat>> getSeatsByBus(
            @PathVariable Long busId) {

        return ResponseEntity.ok(
                seatService.getSeatsByBus(busId)
        );
    }

    @GetMapping("/bus/{busId}/available")
    public ResponseEntity<List<Seat>> getAvailableSeats(
            @PathVariable Long busId) {

        return ResponseEntity.ok(
                seatService.getAvailableSeats(busId)
        );
    }

    @PostMapping("/generate/{busId}")
    public ResponseEntity<?> generateSeats(
            @PathVariable Long busId) {

        try {
            List<Seat> seats =
                    seatService.generateSeatsForBus(busId);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(seats);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{seatId}")
    public ResponseEntity<?> updateSeat(
            @PathVariable Long seatId,
            @RequestBody Seat updatedSeat) {

        try {
            Seat seat =
                    seatService.updateSeat(
                            seatId,
                            updatedSeat
                    );

            return ResponseEntity.ok(seat);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<?> deleteSeat(
            @PathVariable Long seatId) {

        try {
            seatService.deleteSeat(seatId);

            return ResponseEntity.ok(
                    "Seat deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}