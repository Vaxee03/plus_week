package com.example.demo.repository;
import static com.example.demo.entity.QReservation.reservation;
import com.example.demo.entity.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    //QueryDSL 사용해서 메서드 구현
    @Override
    public List<Reservation> searchReservation(Long userId, Long itemId) {
        return jpaQueryFactory.selectFrom(reservation)
                .distinct()
                .leftJoin(reservation.user).fetchJoin()
                .leftJoin(reservation.item).fetchJoin()
                .leftJoin(reservation.item.owner).fetchJoin()
                .leftJoin(reservation.item.manager).fetchJoin()
                .fetch();
    }

    public ReservationRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
}