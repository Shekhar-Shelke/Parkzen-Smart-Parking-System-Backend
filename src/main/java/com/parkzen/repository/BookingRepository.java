package com.parkzen.repository;

import com.parkzen.entity.Booking;
import com.parkzen.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);

    List<Booking> findByParkingSlotParkingAreaOwnerIdOrderByBookingTimeDesc(Long ownerId);

    List<Booking> findByBookingStatus(BookingStatus status);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.parkingSlot.id = :slotId
              AND b.bookingStatus NOT IN ('CANCELLED', 'COMPLETED')
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    List<Booking> findConflictingBookings(@Param("slotId") Long slotId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    long countByParkingSlotParkingAreaOwnerId(Long ownerId);

    long countByBookingStatus(BookingStatus status);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.parkingSlot.parkingArea.owner.id = :ownerId
              AND b.bookingTime >= :from AND b.bookingTime <= :to
            """)
    List<Booking> findOwnerBookingsBetween(@Param("ownerId") Long ownerId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);
}
