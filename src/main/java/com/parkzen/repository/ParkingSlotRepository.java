package com.parkzen.repository;

import com.parkzen.entity.ParkingSlot;
import com.parkzen.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByParkingAreaId(Long parkingAreaId);

    List<ParkingSlot> findByParkingAreaIdAndAvailabilityStatus(Long parkingAreaId, SlotStatus status);

    long countByParkingAreaIdAndAvailabilityStatus(Long parkingAreaId, SlotStatus status);

    @Query("""
            SELECT s FROM ParkingSlot s
            WHERE s.parkingArea.id = :parkingAreaId
              AND s.availabilityStatus = 'AVAILABLE'
              AND s.id NOT IN (
                  SELECT b.parkingSlot.id FROM Booking b
                  WHERE b.bookingStatus NOT IN ('CANCELLED', 'COMPLETED')
                    AND b.startTime < :endTime
                    AND b.endTime > :startTime
              )
            """)
    List<ParkingSlot> findAvailableSlots(@Param("parkingAreaId") Long parkingAreaId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
}
