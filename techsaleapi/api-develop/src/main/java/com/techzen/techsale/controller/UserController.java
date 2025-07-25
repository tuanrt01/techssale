package com.techzen.techsale.controller;

import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController extends AbstractController {
    private final UserService userService;

    @GetMapping("/user-assign")
    public ResponseEntity<List<UserDTO>> getAllUserAssignList() {
        return ok(userService.getAllUserAssignList());
    }

    @GetMapping("/buyer-list")
    public ResponseEntity<List<UserDTO>> getAllBuyerList(){
        return ok(userService.getAllBuyerList());
    }
}
