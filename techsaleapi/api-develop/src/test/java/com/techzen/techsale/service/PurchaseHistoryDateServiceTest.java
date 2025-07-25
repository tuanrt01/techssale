package com.techzen.techsale.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.techzen.techsale.common.SendNotifyMattermost;
import com.techzen.techsale.dto.RequestPurchasedDTO;
import com.techzen.techsale.entity.RequestApplicationEntity;
import com.techzen.techsale.entity.RequestApplicationHistoryEntity;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import com.techzen.techsale.mapper.RequestApplicationMapper;
import com.techzen.techsale.repository.IRequestApplicationRepository;
import com.techzen.techsale.repository.RequestApplicationHistoryRepository;
import com.techzen.techsale.repository.UserRepository;
import com.techzen.techsale.security.UserPrincipal;
import com.techzen.techsale.service.impl.RequestApplicationServiceImpl;
import com.techzen.techsale.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PurchaseHistoryDateServiceTest {

    @Mock
    private IRequestApplicationRepository iRequestApplicationRepository;

    @Mock
    private RequestApplicationMapper requestApplicationMapper;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private RequestApplicationHistoryRepository requestApplicationHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SendNotifyMattermost sendNotifyMattermost;

    @InjectMocks
    private RequestApplicationServiceImpl requestApplicationService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(requestApplicationService, "linkAdmin", "http://techsale.techzen.vn/admin");
        ReflectionTestUtils.setField(requestApplicationService, "linkUser", "http://techsale.techzen.vn");
        ReflectionTestUtils.setField(requestApplicationService, "buyerOrderMsg", "Buyer %s has ordered %s for user %s (%s) approved by %s (%s)");
        ReflectionTestUtils.setField(requestApplicationService, "buyerAcceptMsg", "Buyer completed request");
    }

    @Test
    // khi người mua đặt hàng thì chuyeenr trạng thais
    // hệ thống tự đôg thiết lập ngày đặt hàng
    void buyerOrderRequest_ShouldSetOrderingDate() {
        // Arrange
        int requestId = 42;
        LocalDateTime updatedAt = LocalDateTime.now();
        
        RequestApplicationEntity requestEntity = new RequestApplicationEntity();
        requestEntity.setId(requestId);
        requestEntity.setStatus(RequestStatusEnum.PROCESSING);
        requestEntity.setUpdatedAt(updatedAt);
        requestEntity.setRequestProductName("Test Product");
        requestEntity.setRequestUserId("user123");
        requestEntity.setApproverUserId("approver123");
        
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId("buyer123");
        userPrincipal.setMattermostName("BuyerName");
        
        when(securityUtils.getCurrentUserInfo()).thenReturn(userPrincipal);
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(requestId)).thenReturn(Optional.of(requestEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(requestApplicationMapper.mapToRequestHistory(any(RequestApplicationEntity.class))).thenReturn(new RequestApplicationHistoryEntity());
        when(requestApplicationHistoryRepository.save(any(RequestApplicationHistoryEntity.class))).thenReturn(new RequestApplicationHistoryEntity());
        when(userRepository.getUserMattermostName(anyString())).thenReturn("UserName");

        // Act
        requestApplicationService.buyerOrderRequest(updatedAt, requestId);

        // Assert
        ArgumentCaptor<RequestApplicationEntity> entityCaptor = ArgumentCaptor.forClass(RequestApplicationEntity.class);
        verify(iRequestApplicationRepository).save(entityCaptor.capture());
        RequestApplicationEntity savedEntity = entityCaptor.getValue();
        
        assertEquals(RequestStatusEnum.ORDERING, savedEntity.getStatus());
        assertEquals(userPrincipal.getId(), savedEntity.getBuyerUserId());
        assertNotNull(savedEntity.getOrderingDate());
        
        // The ordering date should be approximately now
        LocalDateTime now = LocalDateTime.now();
        long secondsDiff = ChronoUnit.SECONDS.between(savedEntity.getOrderingDate(), now);
        assertTrue(Math.abs(secondsDiff) < 5, "Ordering date should be within 5 seconds of current time");
    }

    @Test
    // khi người dùng hoàn thành đơn hàng chuển đổi trag thái
    // thiết lập ngày giao hàng và đặt hàng
    void createBuyerRequestPurchased_ShouldSetOrderingAndDeliveryDates() throws IOException {
        // Arrange
        int requestId = 42;
        RequestPurchasedDTO requestPurchasedDTO = RequestPurchasedDTO.builder()
                .boughtProductName("Purchased Product")
                .boughtPlace("Store")
                .boughtAmount(1)
                .boughtPrice(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .build();
        
        RequestApplicationEntity requestEntity = new RequestApplicationEntity();
        requestEntity.setId(requestId);
        requestEntity.setStatus(RequestStatusEnum.ORDERING);
        requestEntity.setUpdatedAt(requestPurchasedDTO.getUpdatedAt());
        requestEntity.setRequestProductName("Test Product");
        requestEntity.setRequestUserId("user123");
        
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(requestId)).thenReturn(Optional.of(requestEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(requestApplicationMapper.mapToRequestHistory(any(RequestApplicationEntity.class))).thenReturn(new RequestApplicationHistoryEntity());
        when(requestApplicationHistoryRepository.save(any(RequestApplicationHistoryEntity.class))).thenReturn(new RequestApplicationHistoryEntity());

        // Act
        requestApplicationService.createBuyerRequestPurchased(requestPurchasedDTO, requestId);

        // Assert
        ArgumentCaptor<RequestApplicationEntity> entityCaptor = ArgumentCaptor.forClass(RequestApplicationEntity.class);
        verify(iRequestApplicationRepository).save(entityCaptor.capture());
        RequestApplicationEntity savedEntity = entityCaptor.getValue();
        
        assertEquals(RequestStatusEnum.SUCCESS, savedEntity.getStatus());
        assertNotNull(savedEntity.getDeliveryDate(), "Delivery date should be set");
        
        // The delivery date should be approximately now
        LocalDateTime now = LocalDateTime.now();
        long secondsDiff = ChronoUnit.SECONDS.between(savedEntity.getDeliveryDate(), now);
        assertTrue(Math.abs(secondsDiff) < 5, "Delivery date should be within 5 seconds of current time");
    }
    
    @Test
    // neeus kh có ngày đặt hàng thì tự động tạo ngày đặt hàng cách 2-5 ngày không
    void createBuyerRequestPurchased_ShouldSetOrderingDateIfNull() throws IOException {
        // Arrange
        int requestId = 42;
        RequestPurchasedDTO requestPurchasedDTO = RequestPurchasedDTO.builder()
                .boughtProductName("Purchased Product")
                .boughtPlace("Store")
                .boughtAmount(1)
                .boughtPrice(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .build();
        
        RequestApplicationEntity requestEntity = new RequestApplicationEntity();
        requestEntity.setId(requestId);
        requestEntity.setStatus(RequestStatusEnum.ORDERING);
        requestEntity.setUpdatedAt(requestPurchasedDTO.getUpdatedAt());
        requestEntity.setOrderingDate(null); // Explicitly set to null
        requestEntity.setRequestProductName("Test Product");
        requestEntity.setRequestUserId("user123");
        
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(requestId)).thenReturn(Optional.of(requestEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(requestApplicationMapper.mapToRequestHistory(any(RequestApplicationEntity.class))).thenReturn(new RequestApplicationHistoryEntity());
        when(requestApplicationHistoryRepository.save(any(RequestApplicationHistoryEntity.class))).thenReturn(new RequestApplicationHistoryEntity());

        // Act
        requestApplicationService.createBuyerRequestPurchased(requestPurchasedDTO, requestId);

        // Assert
        ArgumentCaptor<RequestApplicationEntity> entityCaptor = ArgumentCaptor.forClass(RequestApplicationEntity.class);
        verify(iRequestApplicationRepository).save(entityCaptor.capture());
        RequestApplicationEntity savedEntity = entityCaptor.getValue();
        
        assertNotNull(savedEntity.getOrderingDate(), "Ordering date should be set when null");
        assertNotNull(savedEntity.getDeliveryDate(), "Delivery date should be set");
        
        // The ordering date should be 2-5 days before delivery date
        LocalDateTime deliveryDate = savedEntity.getDeliveryDate();
        LocalDateTime orderingDate = savedEntity.getOrderingDate();
        
        long daysDiff = ChronoUnit.DAYS.between(orderingDate, deliveryDate);
        assertTrue(daysDiff >= 2 && daysDiff <= 5, "Ordering date should be 2-5 days before delivery date");
    }
    
    @Test
    // nếu đơn hàng có sẵn ngày đặt hàng thì có tự động lưu ngày đặt hàng không
    void createBuyerRequestPurchased_ShouldPreserveExistingOrderingDate() throws IOException {
        // Arrange
        int requestId = 42;
        LocalDateTime existingOrderingDate = LocalDateTime.now().minusDays(3);
        
        RequestPurchasedDTO requestPurchasedDTO = RequestPurchasedDTO.builder()
                .boughtProductName("Purchased Product")
                .boughtPlace("Store")
                .boughtAmount(1)
                .boughtPrice(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .build();
        
        RequestApplicationEntity requestEntity = new RequestApplicationEntity();
        requestEntity.setId(requestId);
        requestEntity.setStatus(RequestStatusEnum.ORDERING);
        requestEntity.setUpdatedAt(requestPurchasedDTO.getUpdatedAt());
        requestEntity.setOrderingDate(existingOrderingDate); // Set existing ordering date
        requestEntity.setRequestProductName("Test Product");
        requestEntity.setRequestUserId("user123");
        
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(requestId)).thenReturn(Optional.of(requestEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(requestApplicationMapper.mapToRequestHistory(any(RequestApplicationEntity.class))).thenReturn(new RequestApplicationHistoryEntity());
        when(requestApplicationHistoryRepository.save(any(RequestApplicationHistoryEntity.class))).thenReturn(new RequestApplicationHistoryEntity());

        // Act
        requestApplicationService.createBuyerRequestPurchased(requestPurchasedDTO, requestId);

        // Assert
        ArgumentCaptor<RequestApplicationEntity> entityCaptor = ArgumentCaptor.forClass(RequestApplicationEntity.class);
        verify(iRequestApplicationRepository).save(entityCaptor.capture());
        RequestApplicationEntity savedEntity = entityCaptor.getValue();
        
        assertEquals(existingOrderingDate, savedEntity.getOrderingDate(), "Existing ordering date should be preserved");
        assertNotNull(savedEntity.getDeliveryDate(), "Delivery date should be set");
        
        // The delivery date should be after ordering date
        assertTrue(savedEntity.getDeliveryDate().isAfter(savedEntity.getOrderingDate()), 
                "Delivery date should be after ordering date");
    }
} 