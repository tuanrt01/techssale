package com.techzen.techsale.service;


import com.techzen.techsale.dto.UserDTO;
import java.util.List;

public interface UserService {

    List<UserDTO> getAllUserAssignList();

    List<UserDTO> getAllBuyerList();
}
