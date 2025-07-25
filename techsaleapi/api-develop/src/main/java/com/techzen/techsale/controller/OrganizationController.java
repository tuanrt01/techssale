package com.techzen.techsale.controller;

import com.techzen.techsale.common.JsonResponse;
import com.techzen.techsale.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrganizationController extends AbstractController {
    private final OrganizationService organizationService;

    @GetMapping(value = "/list-organization")
    public JsonResponse getGroupReviewListParent() {
        return new JsonResponse().success(organizationService.findAllParent());
    }
}
