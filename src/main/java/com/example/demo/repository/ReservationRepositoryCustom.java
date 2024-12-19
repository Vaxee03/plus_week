package com.example.demo.repository;

import com.example.demo.entity.Reservation;

import java.util.List;

public interface ReservationRepositoryCustom {
    List<Reservation> searchReservation(Long userId, Long itemId);
}
