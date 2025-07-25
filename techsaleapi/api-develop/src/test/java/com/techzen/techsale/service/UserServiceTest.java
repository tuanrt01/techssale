package com.techzen.techsale.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.repository.UserRepository;
import com.techzen.techsale.service.impl.UserServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUserAssignList_OK() {
        List<UserDTO> expect = new ArrayList<>();
        expect.add(mockUserAssign());

        when(userRepository.findAllUserAssign()).thenReturn(expect);

        List<UserDTO> actual = userService.getAllUserAssignList();

        assertEquals(expect, actual);
    }
    @Test
    void getAllBuyerList_OK() {
        List<UserDTO> expect = new ArrayList<>();
        expect.add(mockBuyer());
        when(userRepository.findAllBuyer()).thenReturn(expect);
        List<UserDTO> actual = userService.getAllBuyerList();
        assertEquals(expect, actual);
    }

    private UserDTO mockBuyer () {
        UserDTO buyerList = new UserDTO();
        buyerList.setId("2505832772459823104");
        buyerList.setName("Nguyễn Lê Anh Quân");
        return buyerList;
    }
    private UserDTO mockUserAssign() {
        UserDTO userAssign = new UserDTO();
        userAssign.setId("2282489122897526784");
        userAssign.setName("Lê Duy Linh");

        return userAssign;
    }
}
