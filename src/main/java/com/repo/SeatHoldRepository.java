package com.repo;


import com.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Integer> {

    List<SeatHold> findByStatus(String status);

    @Query("select seatHold from SeatHold seatHold where seatHold.status = :status and seatHold.holdPlacedTime <  :expiryHoldTime ")
    public List<SeatHold> findByHoldStatusExpiry(@Param("status") String status, @Param("expiryHoldTime") Instant expiryHoldTime);

}
