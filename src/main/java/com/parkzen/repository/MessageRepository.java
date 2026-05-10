package com.parkzen.repository;

import com.parkzen.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderRoleOrReceiverRoleOrderBySentAtDesc(String senderRole, String receiverRole);
    List<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId);
}
