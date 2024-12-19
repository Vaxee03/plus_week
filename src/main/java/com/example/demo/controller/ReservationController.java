package com.example.demo.controller;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public void createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        reservationService.createReservation(reservationRequestDto.getItemId(),
                                            reservationRequestDto.getUserId(),
                                            reservationRequestDto.getStartAt(),
                                            reservationRequestDto.getEndAt());
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<ReservationResponseDto> updateReservation(@PathVariable Long id, @RequestBody ReservationStatus status) {

        ReservationResponseDto ReservationResponseDto =  reservationService.updateReservationStatus(id, status);

        return new ResponseEntity<>(ReservationResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public void findAll() {
        reservationService.getReservations();
    }

    @GetMapping("/search")
    public void searchAll(@RequestParam(required = false) Long userId,
                          @RequestParam(required = false) Long itemId) {
        reservationService.searchAndConvertReservations(userId, itemId);
    }
}
