package com.techzen.techsale.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetAllUserAssignList_OK() throws Exception {
        List<UserDTO> mockList = new ArrayList<>();
        mockList.add(mockUserAssign());

        when(userService.getAllUserAssignList()).thenReturn(mockList);

        this.mockMvc.perform(get("/client-api/v1/user-assign")).andExpect(status().isOk());
    }

    private UserDTO mockUserAssign() {
        UserDTO userAssign = new UserDTO();
        userAssign.setId("2282489122897526784");
        userAssign.setName("Lê Duy Linh");

        return userAssign;
    }
}
