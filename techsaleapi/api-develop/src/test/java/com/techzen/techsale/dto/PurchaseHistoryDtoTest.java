package com.techzen.techsale.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

class PurchaseHistoryDtoTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void requestApplicationForListDTO_ShouldIncludeDeliveryAndOrderingDateFields() {
        // Arrange
        String id = UUID.randomUUID().toString();
        UserDTO userDTO = new UserDTO(id, "Test User", null);
        LocalDateTime now = LocalDateTime.now();
        
        RequestApplicationForListDTO dto = new RequestApplicationForListDTO();
        dto.setId(1);
        dto.setProductName("Test Product");
        dto.setStatus(RequestStatusEnum.SUCCESS);
        dto.setUserRequest(userDTO);
        dto.setOrderingDate(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setDeliveryDate(now.plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Assert
        assertNotNull(dto.getOrderingDate(), "OrderingDate should be present in DTO");
        assertNotNull(dto.getDeliveryDate(), "DeliveryDate should be present in DTO");
    }
    
    @Test
    void requestApplicationDetailResponse_ShouldIncludeDeliveryAndOrderingDateFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        RequestApplicationDetailResponse response = RequestApplicationDetailResponse.builder()
                .id(1)
                .status(RequestStatusEnum.SUCCESS)
                .requestProductName("Test Product")
                .createAt(now.minusDays(5))
                .updatedAt(now)
                .orderingDate(now.minusDays(3))
                .deliveryDate(now)
                .build();
        
        // Assert
        assertNotNull(response.getOrderingDate(), "OrderingDate should be present in detail response");
        assertNotNull(response.getDeliveryDate(), "DeliveryDate should be present in detail response");
    }
    
    @Test
    void calculateDeliveryTime_ShouldReturnCorrectDuration() {
        // Arrange
        LocalDateTime orderingDate = LocalDateTime.now().minusDays(4);
        LocalDateTime deliveryDate = LocalDateTime.now();
        
        // Act - Simulating the calculation that would be done in the frontend
        long daysDifference = calculateDaysBetween(orderingDate, deliveryDate);
        
        // Assert
        assertEquals(4, daysDifference, "Should calculate correct number of days between dates");
    }
    
    private long calculateDaysBetween(LocalDateTime start, LocalDateTime end) {
        // Simple helper method to calculate days between dates
        return java.time.Duration.between(
                start.toLocalDate().atStartOfDay(),
                end.toLocalDate().atStartOfDay())
                .toDays();
    }
} 