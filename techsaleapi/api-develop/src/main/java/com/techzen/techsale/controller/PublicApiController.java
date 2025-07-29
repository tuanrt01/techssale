package com.techzen.techsale.controller;

import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.dto.response.RequestApplicationForListResponse;
import com.techzen.techsale.service.RequestApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/public-api/v1")
public class PublicApiController extends AbstractController {

    private final RequestApplicationService requestApplicationService;

    @GetMapping("/purchase-history")
    public ResponseEntity<RequestApplicationForListResponse> getPurchaseHistory(SearchRequest search) {
        return ok(requestApplicationService.getRequestList(search, false));
    }
    
    @GetMapping("/purchase-history/{id}")
    public ResponseEntity<Object> getPurchaseHistoryDetail(@PathVariable int id) {
        try {
            return ok(requestApplicationService.getRequestApplicationDetail(id));
        } catch (Exception e) {
            log.error("Error getting purchase history detail: ", e);
            return ResponseEntity.badRequest().body("Error getting purchase history detail");
        }
    }
} 