package com.parkzen.repository;

import com.parkzen.entity.ParkingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {

    List<ParkingArea> findByOwnerId(Long ownerId);

    List<ParkingArea> findByStatus(String status);

    @Query(value = """
            SELECT p.* FROM parking_areas p
            WHERE p.status = 'ACTIVE'
              AND p.available_slots > 0
              AND (6371 * acos(
                    cos(radians(:lat)) * cos(radians(p.latitude)) *
                    cos(radians(p.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(p.latitude))
                  )) <= :radiusKm
            ORDER BY (6371 * acos(
                    cos(radians(:lat)) * cos(radians(p.latitude)) *
                    cos(radians(p.longitude) - radians(:lng)) +
                    sin(radians(:lat)) * sin(radians(p.latitude))
                  )) ASC
            """, nativeQuery = true)
    List<ParkingArea> findNearbyParkingAreas(@Param("lat") double lat,
                                              @Param("lng") double lng,
                                              @Param("radiusKm") double radiusKm);
}
