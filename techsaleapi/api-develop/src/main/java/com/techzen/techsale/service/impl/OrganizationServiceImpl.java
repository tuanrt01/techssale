package com.techzen.techsale.service.impl;

import com.techzen.techsale.dto.OrganizationDTO;
import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.entity.OrganizationEntity;
import com.techzen.techsale.repository.OrganizationRepository;
import com.techzen.techsale.repository.UserRepository;
import com.techzen.techsale.service.OrganizationService;
import com.techzen.techsale.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    public List<OrganizationDTO> findAllParent() {
        List<OrganizationEntity> allOrgs = organizationRepository.findByOrganizationParent();
        List<OrganizationDTO> dtos = allOrgs.stream()
                .map(org -> OrganizationDTO.builder()
                        .id(org.getId())
                        .organizationName(org.getOrganizationName())
                        .totalEmployees(0L)
                        .employeesLimit(org.getEmployeesLimit())
                        .validityStartDate(org.getValidityStartDate())
                        .createDate(org.getCreateDate())
                        .updateDate(org.getUpdateDate())
                        .leader(org.getLeader() != null ? org.getLeader().getName() : null)
                        .parent(org.getParent() != null ? org.getParent().getId() : null)
                        .addUser(org.getAddUser())
                        .children(new ArrayList<>())
                        .build())
                .collect(Collectors.toList());

        Map<Long, List<OrganizationDTO>> orgMap = new HashMap<>();
        for (OrganizationDTO org : dtos) {
            Long parentId = org.getParent();
            if (!orgMap.containsKey(parentId)) {
                orgMap.put(parentId, new ArrayList<>());
            }
            orgMap.get(parentId).add(org);
        }

        for (OrganizationDTO org : dtos) {
            List<OrganizationDTO> children = orgMap.get(org.getId());
            if (children != null) {
                org.setChildren(children);
            }
        }

        return orgMap.get(null) != null ? orgMap.get(null) : new ArrayList<>();
    }

}
