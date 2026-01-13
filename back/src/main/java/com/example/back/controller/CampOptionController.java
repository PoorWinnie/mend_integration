package com.example.back.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.SimpleOptionDTO;
import com.example.back.repository.BathroomRepository;
import com.example.back.repository.FacilityRepository;
import com.example.back.repository.ParkingRepository;
import com.example.back.repository.PetRepository;
import com.example.back.repository.WifiRepository;

@RestController
@RequestMapping("/api/public/camp-areas/options")
public class CampOptionController {

    @Autowired
    private WifiRepository wifiRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private BathroomRepository bathroomRepository;

    @Autowired
    private PetRepository petRepository;

    @GetMapping("/wifi")
    public List<SimpleOptionDTO> getAllWifiOptions() {
        return wifiRepository.findAll().stream()
                .map(wifi -> new SimpleOptionDTO(wifi.getWifiId(), wifi.getWifiName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/parking")
    public List<SimpleOptionDTO> getAllParkingOptions() {
        return parkingRepository.findAll().stream()
                .map(parking -> new SimpleOptionDTO(parking.getParkingId(), parking.getParkingName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/facility")
    public List<SimpleOptionDTO> getAllFacilityOptions() {
        return facilityRepository.findAll().stream()
                .map(facility -> new SimpleOptionDTO(facility.getFacilityId(), facility.getFacilityName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/bathroom")
    public List<SimpleOptionDTO> getAllBathroomOptions() {
        return bathroomRepository.findAll().stream()
                .map(bathroom -> new SimpleOptionDTO(bathroom.getBathroomId(), bathroom.getBathroomName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/pet-policy")
    public List<SimpleOptionDTO> getAllPetPolicyOptions() {
        return petRepository.findAll().stream()
                .map(pet -> new SimpleOptionDTO(pet.getPolicyId(), pet.getPolicyName()))
                .collect(Collectors.toList());
    }
}
