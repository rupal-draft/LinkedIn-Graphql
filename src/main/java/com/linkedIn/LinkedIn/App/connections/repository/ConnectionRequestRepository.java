package com.linkedIn.LinkedIn.App.connections.repository;

import com.linkedIn.LinkedIn.App.connections.entity.ConnectionRequest;
import com.linkedIn.LinkedIn.App.connections.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    Optional<ConnectionRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<ConnectionRequest> findAllByReceiverIdAndStatus(Long receiverId, RequestStatus status);

    List<ConnectionRequest> findAllBySenderIdAndStatus(Long senderId, RequestStatus status);

    @Modifying
    @Query("UPDATE ConnectionRequest c SET c.status = ?2 WHERE c.id = ?1")
    void updateConnectionRequestStatus(Long requestId, RequestStatus status);
}
