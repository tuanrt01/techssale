package com.techzen.techsale.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techzen.techsale.dto.RequestApplicationForListDTO;
import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class DeliveryTimeCalculationComprehensiveTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime baseDate = LocalDateTime.of(2025, 7, 1, 10, 0, 0);

    @ParameterizedTest(name = "[{index}] {0}: ordering={1}, delivery={2} → {3}")
    @MethodSource("provideDeliveryTimeTestCases")
    @DisplayName("Test tính toán thời gian giao hàng với nhiều tình huống")
    void calculateDeliveryTime_WithVariousCases(String testName, RequestStatusEnum status,
                                                String orderingDateStr, String deliveryDateStr,
                                                String expectedOutput) {
        // Arrange
        RequestApplicationForListDTO item = new RequestApplicationForListDTO();
        item.setId(1);
        item.setStatus(status);
        item.setProductName("Test Product");
        item.setUserRequest(new UserDTO("1", "Test User", null));

        // Set ngày tháng (nếu có)
        if (orderingDateStr != null) {
            item.setOrderingDate(orderingDateStr);
        }
        if (deliveryDateStr != null) {
            item.setDeliveryDate(deliveryDateStr);
        }

        // Cung cấp createdAt và updatedAt cho trường hợp fallback
        item.setCreatedAt(baseDate.format(formatter));

        // Act
        String result = calculateDeliveryTime(item);

        // Assert
        assertEquals(expectedOutput, result, "Kết quả tính toán thời gian giao hàng không đúng cho: " + testName);
    }

    static Stream<Arguments> provideDeliveryTimeTestCases() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime baseDate = LocalDateTime.of(2025, 7, 1, 10, 0, 0);

        return Stream.of(
                // 1. ĐƠN HÀNG THÀNH CÔNG (SUCCESS)

                // 1.1 Giao hàng sau 1-7 ngày
                Arguments.of("SUCCESS - Giao sau 1 ngày", RequestStatusEnum.SUCCESS,
                        baseDate.format(formatter),
                        baseDate.plusDays(1).format(formatter),
                        "1 day"),
                Arguments.of("SUCCESS - Giao sau 2 ngày", RequestStatusEnum.SUCCESS,
                        baseDate.format(formatter),
                        baseDate.plusDays(2).format(formatter),
                        "2 days"),
                Arguments.of("SUCCESS - Giao sau 7 ngày", RequestStatusEnum.SUCCESS,
                        baseDate.format(formatter),
                        baseDate.plusDays(7).format(formatter),
                        "7 days"),

                // 1.2 Giao trong cùng ngày
                Arguments.of("SUCCESS - Giao cùng ngày", RequestStatusEnum.SUCCESS,
                        baseDate.format(formatter),
                        baseDate.plusHours(5).format(formatter),
                        "Same day"),

                // 1.3 Thời gian giao hàng nhưng thiếu ngày đặt
                Arguments.of("SUCCESS - Có ngày giao nhưng không có ngày đặt", RequestStatusEnum.SUCCESS,
                        null,
                        baseDate.plusDays(3).format(formatter),
                        "-"),

                // 2. ĐƠN HÀNG ĐANG THỰC HIỆN (ORDERING)

                // 2.1 Đơn hàng đang được xử lý trong 1-7 ngày
                Arguments.of("ORDERING - Đã đặt được 1 ngày", RequestStatusEnum.ORDERING,
                        baseDate.minusDays(1).format(formatter),
                        null,
                        "1 day in progress"),
                Arguments.of("ORDERING - Đã đặt được 3 ngày", RequestStatusEnum.ORDERING,
                        baseDate.minusDays(3).format(formatter),
                        null,
                        "3 days in progress"),
                Arguments.of("ORDERING - Đã đặt được 7 ngày", RequestStatusEnum.ORDERING,
                        baseDate.minusDays(7).format(formatter),
                        null,
                        "7 days in progress"),

                // 2.2 Đơn hàng mới đặt hôm nay
                Arguments.of("ORDERING - Mới đặt hôm nay", RequestStatusEnum.ORDERING,
                        baseDate.format(formatter),
                        null,
                        "Today"),

                // 2.3 Thiếu ngày đặt hàng
                Arguments.of("ORDERING - Không có ngày đặt", RequestStatusEnum.ORDERING,
                        null,
                        null,
                        "-"),

                // 3. ĐƠN HÀNG ĐANG XỬ LÝ (PROCESSING)

                // 3.1 Đơn hàng đang xử lý (không tính thời gian giao)
                Arguments.of("PROCESSING - Chưa có thời gian giao", RequestStatusEnum.PROCESSING,
                        null,
                        null,
                        "-"),
                Arguments.of("PROCESSING - Có ngày đặt nhưng chưa giao", RequestStatusEnum.PROCESSING,
                        baseDate.format(formatter),
                        null,
                        "-"),

                // 4. ĐƠN HÀNG CHỜ XỬ LÝ (WAITING)

                Arguments.of("WAITING - Chờ xử lý", RequestStatusEnum.WAITING,
                        null,
                        null,
                        "-"),
                Arguments.of("WAITING - Có ngày đặt", RequestStatusEnum.WAITING,
                        baseDate.format(formatter),
                        null,
                        "-"),

                // 5. ĐƠN HÀNG BỊ TỪ CHỐI (REJECTED)

                Arguments.of("REJECTED - Đơn bị từ chối", RequestStatusEnum.REJECTED,
                        baseDate.format(formatter),
                        null,
                        "-"),
                Arguments.of("REJECTED - Đơn bị từ chối sau khi đặt", RequestStatusEnum.REJECTED,
                        baseDate.format(formatter),
                        baseDate.plusDays(1).format(formatter),
                        "-"),

                // 6. CÁC TRƯỜNG HỢP NGÀY THÁNG ĐẶC BIỆT

                // 6.1 Ngày đặt sau ngày giao (lỗi dữ liệu)
                Arguments.of("Lỗi - Ngày đặt sau ngày giao", RequestStatusEnum.SUCCESS,
                        baseDate.plusDays(5).format(formatter),
                        baseDate.format(formatter),
                        "-5 days"),

                // 6.2 Định dạng ngày không hợp lệ
                Arguments.of("Lỗi - Định dạng ngày không hợp lệ", RequestStatusEnum.SUCCESS,
                        "2025-07-01",  // Thiếu phần giờ
                        baseDate.format(formatter),
                        "-"),
                Arguments.of("Lỗi - Thời gian giao quá lâu (>100 ngày)", RequestStatusEnum.SUCCESS,
                        baseDate.format(formatter),
                        baseDate.plusDays(150).format(formatter),
                        "150 days")
        );
    }

    /**
     * Phương thức mô phỏng logic tính toán thời gian giao hàng như trong frontend
     */
    private String calculateDeliveryTime(RequestApplicationForListDTO item) {
        try {
            // Chỉ tính thời gian cho đơn hàng SUCCESS hoặc ORDERING
            if (item.getStatus() == RequestStatusEnum.SUCCESS && item.getOrderingDate() != null && item.getDeliveryDate() != null) {
                LocalDateTime orderingDateTime = LocalDateTime.parse(item.getOrderingDate(), formatter);
                LocalDateTime deliveryDateTime = LocalDateTime.parse(item.getDeliveryDate(), formatter);

                // Tính số ngày chênh lệch
                long diffDays = ChronoUnit.DAYS.between(orderingDateTime.toLocalDate(), deliveryDateTime.toLocalDate());

                if (diffDays == 0) {
                    return "Same day";
                } else if (diffDays == 1) {
                    return "1 day";
                } else {
                    return diffDays + " days";
                }
            }
            // Cho đơn hàng đang trong quá trình giao
            else if (item.getStatus() == RequestStatusEnum.ORDERING && item.getOrderingDate() != null) {
                LocalDateTime orderingDateTime = LocalDateTime.parse(item.getOrderingDate(), formatter);
                LocalDateTime today = baseDate; // Sử dụng ngày cố định để test

                // Tính số ngày đã trôi qua kể từ khi đặt hàng
                long diffDays = ChronoUnit.DAYS.between(orderingDateTime.toLocalDate(), today.toLocalDate());

                if (diffDays == 0) {
                    return "Today";
                } else if (diffDays == 1) {
                    return "1 day in progress";
                } else {
                    return diffDays + " days in progress";
                }
            }
        } catch (Exception e) {
            // Log lỗi
            System.err.println("Error calculating delivery time: " + e.getMessage());
        }

        return "-";
    }
}