package com.parkzen.repository;

import com.parkzen.entity.Complaint;
import com.parkzen.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByOwnerId(Long ownerId);
    long countByComplaintStatus(ComplaintStatus status);
}
