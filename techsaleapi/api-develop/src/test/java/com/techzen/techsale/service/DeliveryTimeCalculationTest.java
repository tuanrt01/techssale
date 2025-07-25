package com.techzen.techsale.service;

import static org.junit.jupiter.api.Assertions.*;

import com.techzen.techsale.dto.RequestApplicationForListDTO;
import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
public class DeliveryTimeCalculationTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Test
    // tính toán khi thời gian khi đơn đã hoàn thành và ddax có ngày đặt ngày giao
    void calculateDeliveryTime_ForCompletedOrder_WithSpecificDates() {
        // Arrange
        RequestApplicationForListDTO item = createRequestDTO(RequestStatusEnum.SUCCESS);
        
        // Set ordering date to 3 days before delivery date
        LocalDateTime deliveryDate = LocalDateTime.now();
        LocalDateTime orderingDate = deliveryDate.minusDays(3);
        
        item.setDeliveryDate(formatter.format(deliveryDate));
        item.setOrderingDate(formatter.format(orderingDate));
        
        // Act
        String result = calculateDeliveryTime(item);
        
        // Assert
        assertEquals("3 days", result, "Should calculate 3 days difference");
    }
    
    @Test
    // Kiểm tra trường hợp đặt hàng và giao hàng trong cùng một ngày
    void calculateDeliveryTime_ForCompletedOrder_SameDay() {
        // Arrange
        RequestApplicationForListDTO item = createRequestDTO(RequestStatusEnum.SUCCESS);
        
        // Set ordering and delivery date to same day
        LocalDateTime now = LocalDateTime.now();
        
        item.setDeliveryDate(formatter.format(now));
        item.setOrderingDate(formatter.format(now));
        
        // Act
        String result = calculateDeliveryTime(item);
        
        // Assert
        assertEquals("Same day", result, "Should show 'Same day' when dates are equal");
    }
    
    @Test
    // Kiểm tra hiển thị thời gian cho đơn hàng đang trong quá trình giao (ORDERING) nhưng chưa hoàn thành.
    void calculateDeliveryTime_ForOrderInProgress() {
        // Arrange
        RequestApplicationForListDTO item = createRequestDTO(RequestStatusEnum.ORDERING);
        
        // Set ordering date to 2 days ago
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderingDate = now.minusDays(2);
        
        item.setOrderingDate(formatter.format(orderingDate));
        item.setDeliveryDate(null);
        
        // Act
        String result = calculateDeliveryTime(item);
        
        // Assert
        assertEquals("2 days in progress", result, "Should show days in progress for ongoing orders");
    }
    
    @Test
    // Kiểm tra logic dự phòng khi không có sẵn ngày đặt và ngày giao cụ thể.
    void calculateDeliveryTime_WhenDatesAreMissing_ShouldUseFallback() {
        // Arrange
        RequestApplicationForListDTO item = createRequestDTO(RequestStatusEnum.SUCCESS);
        
        // Set fallback dates
        LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        item.setCreatedAt(formatter.format(createdAt));
        
        // Act
        String result = calculateDeliveryTimeWithFallback(item, updatedAt);
        
        // Assert
        assertEquals("5 days", result, "Should calculate using fallback dates");
    }
    
    @Test
    //  Kiểm tra hiển thị khi đơn hàng chưa bắt đầu thực hiện (WAITING).
    void calculateDeliveryTime_ForNotStartedOrders() {
        // Arrange
        RequestApplicationForListDTO item = createRequestDTO(RequestStatusEnum.WAITING);
        
        // Act
        String result = calculateDeliveryTime(item);
        
        // Assert
        assertEquals("-", result, "Should show '-' for orders not yet started");
    }
    
    // Helper methods to simulate the frontend logic
    
    private RequestApplicationForListDTO createRequestDTO(RequestStatusEnum status) {
        UserDTO userDTO = new UserDTO("user123", "Test User", null);
        
        RequestApplicationForListDTO dto = new RequestApplicationForListDTO();
        dto.setId(1);
        dto.setStatus(status);
        dto.setProductName("Test Product");
        dto.setUserRequest(userDTO);
        
        return dto;
    }
    
    private String calculateDeliveryTime(RequestApplicationForListDTO item) {
        // Simulating the frontend calculation logic
        try {
            // Only calculate for SUCCESS or ORDERING status
            if (item.getStatus() == RequestStatusEnum.SUCCESS && item.getOrderingDate() != null && item.getDeliveryDate() != null) {
                LocalDateTime orderingDateTime = LocalDateTime.parse(item.getOrderingDate(), formatter);
                LocalDateTime deliveryDateTime = LocalDateTime.parse(item.getDeliveryDate(), formatter);

                // tính khoảng cacsh ngày giuwax chúng
                long diffDays = ChronoUnit.DAYS.between(orderingDateTime, deliveryDateTime);

                if (diffDays == 0) {
                    return "Same day";
                } else {
                    return diffDays + " days";
                }
            }
            // For ongoing orders
            else if (item.getStatus() == RequestStatusEnum.ORDERING && item.getOrderingDate() != null) {
                LocalDateTime orderingDateTime = LocalDateTime.parse(item.getOrderingDate(), formatter);
                LocalDateTime today = LocalDateTime.now();

                // Calculate days in progress
                long diffDays = ChronoUnit.DAYS.between(orderingDateTime, today);

                if (diffDays == 0) {
                    return "Today";
                } else {
                    return diffDays + " days in progress";
                }
            }
        } catch (Exception e) {
            // Log error would happen in frontend
        }

        return "-";
    }

    private String calculateDeliveryTimeWithFallback(RequestApplicationForListDTO item, LocalDateTime updatedAt) {
        // Simulate fallback logic when specific dates aren't available
        try {
            if (item.getCreatedAt() != null) {
                LocalDateTime createdDate = LocalDateTime.parse(item.getCreatedAt(), formatter);

                // Calculate days difference using fallback dates
                long diffDays = ChronoUnit.DAYS.between(createdDate, updatedAt);

                if (diffDays == 0) {
                    return "Same day";
                } else {
                    return diffDays + " days";
                }
            }
        } catch (Exception e) {
            // Log error would happen in frontend
        }

        return "-";
    }
} 