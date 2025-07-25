package com.techzen.techsale.repository;

import com.techzen.techsale.entity.AttachmentsEntity;
import com.techzen.techsale.enumeration.ActionEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentsRepository extends JpaRepository<AttachmentsEntity, Integer> {

    void deleteByRequestId(int requestId);

    AttachmentsEntity findFirstByRequestIdAndActionInOrderByCreateAtDesc(int requestId, List<ActionEnum> action);

}
