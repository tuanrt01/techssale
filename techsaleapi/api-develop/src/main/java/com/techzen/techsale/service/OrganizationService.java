package com.techzen.techsale.service;


import com.techzen.techsale.dto.OrganizationDTO;

import java.util.List;

public interface OrganizationService {

    List<OrganizationDTO> findAllParent();

}
