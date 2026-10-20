package com.namrata.busreservation.service;

import com.namrata.busreservation.entity.Bus;
import com.namrata.busreservation.repository.BusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusService {

    private final BusRepository busRepository;

    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    public Bus addBus(Bus bus) {

        if (busRepository.existsByBusNumber(bus.getBusNumber())) {
            throw new RuntimeException("Bus number already exists");
        }

        return busRepository.save(bus);
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Optional<Bus> getBusById(Long busId) {
        return busRepository.findById(busId);
    }

    public Bus updateBus(Long busId, Bus updatedBus) {

        Bus existingBus = busRepository.findById(busId)
                .orElseThrow(() ->
                        new RuntimeException("Bus not found"));

        existingBus.setBusNumber(updatedBus.getBusNumber());
        existingBus.setBusName(updatedBus.getBusName());
        existingBus.setBusType(updatedBus.getBusType());
        existingBus.setTotalSeats(updatedBus.getTotalSeats());
        existingBus.setOperatorName(updatedBus.getOperatorName());

        return busRepository.save(existingBus);
    }

    public void deleteBus(Long busId) {

        if (!busRepository.existsById(busId)) {
            throw new RuntimeException("Bus not found");
        }

        busRepository.deleteById(busId);
    }
}