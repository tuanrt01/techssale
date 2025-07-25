package com.techzen.techsale.service.impl;

import com.techzen.techsale.dto.ReasonDTO;
import com.techzen.techsale.dto.request.ReasonRequest;
import com.techzen.techsale.entity.ReasonEntity;
import com.techzen.techsale.entity.UserEntity;
import com.techzen.techsale.repository.ReasonRepository;
import com.techzen.techsale.repository.UserRepository;
import com.techzen.techsale.service.ReasonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReasonServiceImpl implements ReasonService {
    private final ReasonRepository reasonRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReasonEntity> getAllReasonList(String reason) {
                List<ReasonEntity> content = reasonRepository.getAllListReasonAndSearch(reason);
        Map<Long, List<ReasonEntity>> orgMap = content.stream()
                .collect(Collectors.groupingBy(
                        o -> (o.getParent() != null) ? o.getParent() : 0L,
                        HashMap::new,
                        Collectors.toList()
                ));
        for (ReasonEntity org : content) {
            if (orgMap.containsKey(org.getId().longValue())) {
                org.setChildren(orgMap.get(org.getId().longValue()));
            } else {
                org.setChildren(new ArrayList<>());
            }
        }
        return content.stream()
                .filter(reasonEntity -> reasonEntity.getParent() == null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<?> insertReason(ReasonRequest dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity userEntity;
            if (authentication == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userEntity = userRepository.findByEmail(userDetails.getUsername());
            ReasonEntity reasonEntity =new ReasonEntity();
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = localDateTime.format(formatter);
            reasonEntity.setCreatedBy(userEntity);
            reasonEntity.setCreatedAt(formattedDateTime);
            reasonEntity.setReason(dto.getReason());
            if(dto.getParent() != null && dto.getParent() != 0) {
                reasonEntity.setParent(dto.getParent());
            }
            UserEntity buyer = userRepository.findById(dto.getMainBuyer()).orElse(null);
            reasonEntity.setBuyerUserId(buyer);
            reasonEntity.setIsRemove(false);
            reasonRepository.save(reasonEntity);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public boolean delete(String id) {
        try{
            ReasonEntity reasonEntity = reasonRepository.findById(Integer.parseInt(id)).get();
            reasonEntity.setIsRemove(true);
            reasonRepository.save(reasonEntity);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    @Override
    public ResponseEntity<?> updateReason(ReasonRequest reason) {
        try {
            ReasonEntity reasonEntity =reasonRepository.findById(reason.getId()).get();
            UserEntity buyer = userRepository.findById(reason.getMainBuyer()).orElse(null);
            reasonEntity.setBuyerUserId(buyer);
            reasonEntity.setReason(reason.getReason());
            reasonRepository.save(reasonEntity);
            List<ReasonEntity> children = reasonRepository.findAll().stream()
                    .filter(r -> reasonEntity.getId().equals(r.getParent()))
                    .collect(Collectors.toList());
            for (ReasonEntity child : children) {
                 if(child.getBuyerUserId() == null) {
                     child.setBuyerUserId(buyer);
                     reasonRepository.save(child);
                 }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ReasonDTO getDetailReason(String id) {
        try {
            if (id == null || id.isEmpty()) {
                return null;
            }
            ReasonEntity reasonEntity = reasonRepository.findById(Integer.parseInt(id)).orElse(null);
            if (reasonEntity == null) return null;

            ReasonDTO dto = new ReasonDTO();
            dto.setId(reasonEntity.getId());
            dto.setReason(reasonEntity.getReason());
            dto.setParent(reasonEntity.getParent());
            dto.setCreatedAt(reasonEntity.getCreatedAt());
            dto.setCreatedBy(reasonEntity.getCreatedBy());
            if (reasonEntity.getChildren() != null && !reasonEntity.getChildren().isEmpty()) {
                List<ReasonDTO> childDTOs = new ArrayList<>();
                for (ReasonEntity childEntity : reasonEntity.getChildren()) {
                    ReasonDTO childDTO = new ReasonDTO();
                    childDTO.setId(childEntity.getId());
                    childDTO.setReason(childEntity.getReason());
                    childDTO.setParent(childEntity.getParent());
                    childDTO.setCreatedAt(childEntity.getCreatedAt());
                    childDTO.setCreatedBy(childEntity.getCreatedBy());
                    childDTO.setChildren(new ArrayList<>());
                    childDTOs.add(childDTO);
                }
                dto.setChildren(childDTOs);
            } else {
                dto.setChildren(new ArrayList<>());
            }
            dto.setMainBuyer(reasonEntity.getBuyerUserId() != null ? reasonEntity.getBuyerUserId().getId() : null);
            return dto;
        } catch (Exception e) {
            return null;
        }
    }
}
