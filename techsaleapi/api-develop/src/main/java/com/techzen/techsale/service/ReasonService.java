package com.techzen.techsale.service;

import com.techzen.techsale.dto.ReasonDTO;
import com.techzen.techsale.dto.request.ReasonRequest;
import com.techzen.techsale.entity.ReasonEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReasonService {
    List<ReasonEntity> getAllReasonList(String reason);

    ResponseEntity<?> insertReason(ReasonRequest reason);

    boolean delete(String id);

    ResponseEntity<?> updateReason(ReasonRequest reason);

    ReasonDTO getDetailReason(String reason);
}
