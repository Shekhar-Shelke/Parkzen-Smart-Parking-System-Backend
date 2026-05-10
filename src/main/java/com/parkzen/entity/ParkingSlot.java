package com.parkzen.entity;

import com.parkzen.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", nullable = false)
    private String slotNumber;

    @Column(name = "slot_type")
    @Builder.Default
    private String slotType = "CAR";

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    @Builder.Default
    private SlotStatus availabilityStatus = SlotStatus.AVAILABLE;

    @Column(name = "charging_enabled")
    @Builder.Default
    private Boolean chargingEnabled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_area_id", nullable = false)
    private ParkingArea parkingArea;

    @OneToMany(mappedBy = "parkingSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
}
