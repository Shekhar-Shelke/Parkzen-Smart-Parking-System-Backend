package com.parkzen.repository;

import com.parkzen.entity.Payment;
import com.parkzen.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    List<Payment> findByOwnerIdAndPaymentStatus(Long ownerId, PaymentStatus status);

    List<Payment> findByOwnerId(Long ownerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.owner.id = :ownerId AND p.paymentStatus = 'SUCCESS'")
    Double sumEarningsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'SUCCESS'")
    Double sumTotalRevenue();
}
