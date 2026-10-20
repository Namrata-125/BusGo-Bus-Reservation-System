package com.namrata.busreservation.service;

import com.namrata.busreservation.entity.Bus;
import com.namrata.busreservation.entity.Seat;
import com.namrata.busreservation.repository.BusRepository;
import com.namrata.busreservation.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final BusRepository busRepository;

    public SeatService(
            SeatRepository seatRepository,
            BusRepository busRepository) {

        this.seatRepository = seatRepository;
        this.busRepository = busRepository;
    }

    public Seat addSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long seatId) {
        return seatRepository.findById(seatId);
    }

    public List<Seat> getSeatsByBus(Long busId) {
        return seatRepository.findByBusBusId(busId);
    }

    public List<Seat> getAvailableSeats(Long busId) {
        return seatRepository.findByBusBusIdAndSeatStatus(
                busId,
                Seat.SeatStatus.AVAILABLE
        );
    }

    public List<Seat> generateSeatsForBus(Long busId) {

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() ->
                        new RuntimeException("Bus not found"));

        int totalSeats = bus.getTotalSeats();

        List<Seat> existingSeats =
                seatRepository.findByBusBusId(busId);

        for (int i = 1; i <= totalSeats; i++) {

            String seatNumber = String.valueOf(i);

            boolean seatExists = existingSeats.stream()
                    .anyMatch(seat ->
                            seat.getSeatNumber().equals(seatNumber));

            if (seatExists) {
                continue;
            }

            Seat seat = new Seat();

            seat.setBus(bus);
            seat.setSeatNumber(seatNumber);

            if (i % 2 == 1) {
                seat.setSeatType(Seat.SeatType.WINDOW);
            } else {
                seat.setSeatType(Seat.SeatType.AISLE);
            }

            seat.setSeatStatus(Seat.SeatStatus.AVAILABLE);

            seatRepository.save(seat);
        }

        return seatRepository.findByBusBusId(busId);
    }

    public Seat updateSeat(
            Long seatId,
            Seat updatedSeat) {

        Seat existingSeat = seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new RuntimeException("Seat not found"));

        existingSeat.setBus(updatedSeat.getBus());
        existingSeat.setSeatNumber(
                updatedSeat.getSeatNumber()
        );
        existingSeat.setSeatType(
                updatedSeat.getSeatType()
        );
        existingSeat.setSeatStatus(
                updatedSeat.getSeatStatus()
        );

        return seatRepository.save(existingSeat);
    }

    public void deleteSeat(Long seatId) {

        if (!seatRepository.existsById(seatId)) {
            throw new RuntimeException("Seat not found");
        }

        seatRepository.deleteById(seatId);
    }
}