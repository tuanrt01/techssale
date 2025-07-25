package com.techzen.techsale.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.techzen.techsale.dto.RejectReasonDTO;
import com.techzen.techsale.dto.RequestApplicationForListDTO;
import com.techzen.techsale.dto.UpdateApprovalDTO;
import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.dto.response.RequestApplicationForListResponse;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import com.techzen.techsale.service.RequestApplicationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RequestApplicationControllerTest {

    private static final String REQUEST_APPLICATION_ENDPOINT = "/client-api/v1/request-application";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestApplicationController requestApplicationController;

    @Mock
    private RequestApplicationService requestApplicationService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestApplicationController).build();
    }

    @Test
    void testGetRequestApplicationDetail_OK() throws Exception {
        when(requestApplicationService.getRequestApplicationDetail(anyInt()))
                .thenReturn(new RequestApplicationDetailResponse());
        this.mockMvc.perform(get(REQUEST_APPLICATION_ENDPOINT + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRequestApplicationDetail_BadRequest() throws Exception {
        this.mockMvc.perform(get(REQUEST_APPLICATION_ENDPOINT + "/notInt"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRequestList_OK() throws Exception {

        RequestApplicationForListResponse response = mockRequestApplicationListResponse();
        when(requestApplicationService.getRequestList(any(SearchRequest.class), any(Boolean.class))).thenReturn(response);

        this.mockMvc.perform(get(REQUEST_APPLICATION_ENDPOINT)
                        .param("size", "2")
                        .param("sort", "ID")
                        .param("page", "0")
                        .param("direction", "ASC")
                        .param("userRequestName", "Hoang")
                        .param("createdAtBegin", "2023-08-02")
                        .param("createdAtEnd", "2023-08-02")
                        .param("expectReceiveDateBegin", "2023-08-02")
                        .param("expectReceiveDateEnd", "2023-08-02")
                        .param("userAssignedName", "Hoang")
                        .param("status", "WAITING")
                        .param("requestReason", "OTHER")
                        .param("isAdmin", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRequestApplicationList_BadRequest() throws Exception {
        this.mockMvc.perform(get(REQUEST_APPLICATION_ENDPOINT +
                        "?size=notInt&sort=invalidField&page=notInt&direction=invalidField"))
                .andExpect(status().isBadRequest());
    }

    private RequestApplicationForListResponse mockRequestApplicationListResponse() {
        List<RequestApplicationForListDTO> content = new ArrayList<>();
        content.add(mockRequestApplicationListDTO());
        RequestApplicationForListResponse response = new RequestApplicationForListResponse();
        response.setContent(content);
        response.setTotalPages(1L);
        response.setTotalElements(1L);
        response.setSize(1);
        response.setPage(0);
        return response;
    }

    private RequestApplicationForListDTO mockRequestApplicationListDTO() {
        UserDTO userRequestDetail = new UserDTO("2386974331516223488",
                "Lê Đức Phúc",
                "https://s3.ap-southeast-1.amazonaws.com/techzai/1689834162950-leducphuc.jpg");
        UserDTO userAssignDetail = new UserDTO(
                "2386973436921511936",
                "Trương Thị Phụng",
                "https://s3.ap-southeast-1.amazonaws.com/techzai/1689834162950-leducphuc.jpg");
        return RequestApplicationForListDTO.builder()
                .id(4)
                .status(RequestStatusEnum.WAITING)
                .expectReceiveDate("2023-08-24T00:00:00")
                .createdAt("2023-08-22T00:00:00")
                .productName("Xúc xích")
                .requestReason("BIRTHDAY")
                .userRequest(userRequestDetail)
                .userAssign(userAssignDetail)
                .build();
    }

    @Test
    void createRequest_OK() throws Exception {
        this.mockMvc.perform(post(REQUEST_APPLICATION_ENDPOINT)
                .param("requestProductName", "Sticker")
                .param("requestReason", "1")
                .param("expectReceiveDate", "2099-08-22T12:00:00")
                .param("approverUserId", "2282489122897526784")
                .param("estimatePrice", "0")
                .param("requestReasonDetail", "Thích mua")
                .param("requestAmount", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    void createRequestPurchased_BadRequest() throws Exception{
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/7/buyer-completion")
                .param("boughtProductName", "")
                .param("boughtPlace", "")
                .param("boughtAmount", "0")
                .param("boughtPrice", "0")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createRequestPurchased_Success() throws Exception {
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/7/buyer-completion")
                .param("boughtProductName", "Máy tính")
                .param("boughtPlace", "Điện Máy Xanh")
                .param("boughtAmount", "1")
                .param("boughtPrice", "20000000")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void createRequestBuyerReject_BadRequest() throws Exception{
        RejectReasonDTO rejectReasonDTO = mockCreateRequestRejectBadRequest();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/7/buyer-rejection")
                        .content(mapper.writeValueAsString(rejectReasonDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequestBuyerReject_Success() throws Exception{
        RejectReasonDTO rejectReasonDTO = mockCreateRequestRejectSuccess();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/7/buyer-rejection")
                        .content(mapper.writeValueAsString(rejectReasonDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
    }

    private RejectReasonDTO mockCreateRequestRejectSuccess(){
        return RejectReasonDTO.builder().rejectReason("Cửa hàng đóng cửa").build();
    }

    private RejectReasonDTO mockCreateRequestRejectBadRequest(){
        return RejectReasonDTO.builder().rejectReason("").build();
    }

    @Test
    void createApprovalRequestReject_BadRequest() throws Exception{
        RejectReasonDTO rejectReasonDTO = mockCreateRequestRejectBadRequest();
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/2/rejection")
                .content(mapper.writeValueAsString(rejectReasonDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }
    @Test
    void createApprovalRequestReject_Success() throws Exception{
        RejectReasonDTO rejectReasonDTO = mockCreateRequestRejectSuccess();
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/2/rejection")
                .content(mapper.writeValueAsString(rejectReasonDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is2xxSuccessful());
    }
    @Test
    void updateApprovalRequestStatus_Success() throws Exception{
        UpdateApprovalDTO updateApprovalDTO = mockUpdateApprovalSuccess();
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/2/approval")
                .content(mapper.writeValueAsString(updateApprovalDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is2xxSuccessful());
    }
    @Test
    void updateApprovalRequestStatus_BadRequest() throws Exception{
        UpdateApprovalDTO updateApprovalDTO = mockUpdateApprovalBadRequest();
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/2/approval")
                .content(mapper.writeValueAsString(updateApprovalDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    private UpdateApprovalDTO mockUpdateApprovalBadRequest(){
        return UpdateApprovalDTO.builder()
            .buyerUserId("")
            .build();
    }
    private UpdateApprovalDTO mockUpdateApprovalSuccess(){
        return UpdateApprovalDTO.builder()
            .buyerUserId("2505813469232627712")
            .build();
    }
    @Test
    void deleteRequest_BadRequest() throws Exception{
        this.mockMvc.perform(delete(REQUEST_APPLICATION_ENDPOINT + "/abc/delete")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }
    @Test
    void deleteRequest_Success() throws Exception{
        this.mockMvc.perform(delete(REQUEST_APPLICATION_ENDPOINT + "/42/delete")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateBuyerOrder_Ok() throws Exception{
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/17/order?updateAt=2023-09-07T09:00:59")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
    }

    @Test
    void updateBuyerOrder_BadRequest() throws Exception{
        this.mockMvc.perform(put(REQUEST_APPLICATION_ENDPOINT + "/a/order?updateAt=2023-09-07 09:00:59")
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
    }
}
