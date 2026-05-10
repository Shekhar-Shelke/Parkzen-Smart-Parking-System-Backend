package com.parkzen.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @Column(name = "available_slots")
    @Builder.Default
    private Integer availableSlots = 0;

    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour;

    @Column(name = "charging_available")
    @Builder.Default
    private Boolean chargingAvailable = false;

    @Column(name = "charging_price_per_hour")
    @Builder.Default
    private Double chargingPricePerHour = 0.0;

    @Builder.Default
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @OneToMany(mappedBy = "parkingArea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ParkingSlot> parkingSlots = new ArrayList<>();
}
