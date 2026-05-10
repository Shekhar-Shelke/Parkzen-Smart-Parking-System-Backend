package com.parkzen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long totalOwners;
    private Long totalBookings;
    private Double totalRevenue;
    private Long activeBookings;
    private Long pendingComplaints;
    private Long totalTransactions;
    private Long pendingOwnerApprovals;
}
