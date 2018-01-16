package com.repo;

import com.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository  extends JpaRepository<Seat, Long> {

  /*  @Modifying
    @Query("update Seat s set s.status = :status  where s.seatHoldId = :seatHoldId")
    void updateSeats(@Param("seatHoldId") int seatHoldId, @Param("status") String status);

    @Modifying @Query("update Seat s set s.status = :status,s.reservationCode = :reservationCode  where s.seatHoldId = :seatHoldId")
    void updateSeatReservation(@Param("seatHoldId") int seatHoldId, @Param("status") String status, @Param("reservationCode") String reservationCode);*/

    List<Seat> findByStatusIn(List<String> seatStatus);

    List<Seat> findBySeatHoldId(Integer seatHoldId);

    List<Seat> findByNumberIn(List<Integer> seatNumbers);

    //Long countByStatusIn(List<String> seatStatus);
}
