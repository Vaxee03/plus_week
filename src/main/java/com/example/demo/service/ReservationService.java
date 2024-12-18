package com.example.demo.service;

import com.example.demo.ReservationStatus;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.dto.UpdateReservationDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.RentalLog;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;


import static com.example.demo.entity.QReservation.reservation;


@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;
    private final JPAQueryFactory jpaQueryFactory;

    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService,
                              JPAQueryFactory jpaQueryFactory) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // TODO: 1. 트랜잭션 이해
    @Transactional
    public void createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
//        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
//        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
//        if(!haveReservations.isEmpty()) {
//            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
//        }

        // 예약 생성 로직
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다."));
        Reservation reservation = new Reservation(item, user, (ReservationStatus.PENDING) , startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 렌탈 로그 생성 및 저장
        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);
    }

    // TODO: 3. N+1 문제
    public List<ReservationResponseDto> getReservations() {
        List<Reservation> reservations = reservationRepository.findAllWithUserAndItem();

        return reservations.stream().map(reservation ->
                new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                )
        ).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        List<Reservation> reservations = searchReservations(userId, itemId);

        return convertToDto(reservations);
    }

    public List<Reservation> searchReservations(Long userId, Long itemId) {

        return jpaQueryFactory.select(reservation)
                .from(reservation)
                .leftJoin(reservation.user).fetchJoin()
                .leftJoin(reservation.item).fetchJoin()
                .where(userId != null ? reservation.user.id.eq(userId) : null,
                        itemId != null ? reservation.item.id.eq(itemId) : null)
                .fetch();
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    @Transactional
    public UpdateReservationDto updateReservationStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = findReservationById(reservationId);

        switch (status) {
            case APPROVED:
                if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
                    throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
                }
                reservation.updateStatus(ReservationStatus.APPROVED);
                break;

            case CANCELED:
                if (ReservationStatus.EXPIRED.equals(reservation.getStatus())) {
                    throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
                }
                reservation.updateStatus(ReservationStatus.CANCELED);
                break;

            case EXPIRED:
                if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
                    throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
                }
                reservation.updateStatus(ReservationStatus.EXPIRED);
                break;

            default:
                throw new IllegalArgumentException("올바르지 않은 상태: " + status);
        }

        return new UpdateReservationDto(reservation);
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID에 맞는 데이터가 존재하지 않습니다."));
    }
}
