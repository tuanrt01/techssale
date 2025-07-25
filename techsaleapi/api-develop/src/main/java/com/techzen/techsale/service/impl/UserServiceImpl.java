package com.techzen.techsale.service.impl;

import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.repository.UserRepository;
import com.techzen.techsale.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUserAssignList() {
        log.info("Start get user assign list");
        return userRepository.findAllUserAssign();
    }

    public List<UserDTO> getAllBuyerList(){
        log.info("Start get user assign list");
        return userRepository.findAllBuyer();
    }
}
