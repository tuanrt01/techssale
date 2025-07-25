package com.techzen.techsale.repository;

import com.techzen.techsale.dto.RequestApplicationDTO;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.entity.RequestApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRequestApplicationRepository extends JpaRepository<RequestApplicationEntity, Integer> {

    @Query("SELECT      new com.techzen.techsale.dto.response.RequestApplicationDetailResponse("
        +  "            a.id, b.id, b.name, a.requestProductName, a.requestAmount, a.estimatePrice, a.requestReason, a.requestReasonDetail, "
        +  "            a.expectReceiveDate, a.description, c.id, c.name, d.id, d.name, a.approverRejectReason, "
        +  "            a.buyerRejectReason, a.boughtProductName, a.boughtPrice, a.boughtPlace, a.boughtAmount, a.status, " +
            " a.createAt, a.createAt, a.updatedAt, a.productLink, e.id, e.name, o.id, o.organizationName, a.statusReject, a.orderingDate, a.deliveryDate ) "
        +  "FROM        RequestApplicationEntity a "
        +  "LEFT JOIN   UserEntity               b ON b.id = a.requestUserId "
        +  "LEFT JOIN   UserEntity               c ON c.id = a.approverUserId "
        +  "LEFT JOIN   UserEntity               d ON d.id = a.buyerUserId "
        +  "LEFT JOIN   UserEntity               e ON e.id = a.personnelUsed "
        +  "LEFT JOIN   OrganizationEntity       o ON o.id = a.organizationUsed "
        +  "WHERE       a.id = :id AND a.isDeleted = false ")
    Optional<RequestApplicationDetailResponse> fetchRequestDetailById(int id);

    Optional<RequestApplicationEntity> findByIdAndIsDeletedIsFalse(int id);


    @Query("SELECT new com.techzen.techsale.dto.RequestApplicationDTO(a.id, a.requestReason, a.requestProductName, b.name) "
            +  "from RequestApplicationEntity a "
            +  "JOIN UserEntity b ON b.mattermostName = a.createdBy "
            +  "WHERE a.requestReason = :reason AND a.isDeleted = false ")
    List<RequestApplicationDTO> getListByReason(String reason);
}
