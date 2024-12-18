package com.example.demo.dto;

import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.Reservation;
import lombok.Getter;

@Getter
public class UpdateReservationDto {
    private final Long reservationId;
    private final ReservationStatus status;

    public UpdateReservationDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.status = reservation.getStatus();
    }
}