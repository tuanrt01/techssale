package com.techzen.techsale.controller;

import com.techzen.techsale.common.JsonResponse;
import com.techzen.techsale.dto.ReasonDTO;
import com.techzen.techsale.dto.RequestApplicationDTO;
import com.techzen.techsale.dto.request.ReasonRequest;
import com.techzen.techsale.entity.ReasonEntity;
import com.techzen.techsale.service.ReasonService;
import com.techzen.techsale.service.RequestApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reason")
@Slf4j
public class ReasonController {
    private final ReasonService reasonService;
    private final RequestApplicationService requestApplicationService;
    private static final String BAD_REQUEST_MSG = "Bad request";
    private static final String ID_NOT_FOUND_MSG = "Id is not found";

    @GetMapping("/list")
    public ResponseEntity<List<ReasonEntity>> getAllReasonList(@RequestParam(name = "reason", defaultValue = "") String reason) {
        return ResponseEntity.ok(reasonService.getAllReasonList(reason));
    }

    @PostMapping("/create")
    public JsonResponse createReason(@RequestBody ReasonRequest reason) {
        if (reason == null) {
            return new JsonResponse().failure(BAD_REQUEST_MSG);
        }

        ResponseEntity<?> response = reasonService.insertReason(reason);
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            return new JsonResponse().failure(response, BAD_REQUEST_MSG);
        }
        return new JsonResponse().success(response);
    }

    @DeleteMapping("/delete/{id}")
    public JsonResponse deleteReason(@PathVariable String id) {
        try {
            if (id == null) {
                return new JsonResponse().failure(ID_NOT_FOUND_MSG);
            }
            boolean checkDelete = reasonService.delete(id);
            if (checkDelete) {
                return new JsonResponse().success();
            }
            return new JsonResponse().failure("Delete failed");
        } catch (Exception e) {
            return new JsonResponse().failure("Invalid Id Format");
        }
    }

    @PutMapping("/update")
    public JsonResponse updateReason(@RequestBody ReasonRequest reason) {
        if (reason == null) {
            return new JsonResponse().failure(BAD_REQUEST_MSG);
        }

        ResponseEntity<?> response = reasonService.updateReason(reason);
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            return new JsonResponse().failure(response, BAD_REQUEST_MSG);
        }
        return new JsonResponse().success(response);

    }

    @GetMapping("/detail")
    public ResponseEntity<ReasonDTO> getDetailReason(@RequestParam(name = "idReason", defaultValue = "") String idReason) {
        try {
            ReasonDTO reasonDTO = reasonService.getDetailReason(idReason);
            if (reasonDTO == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(reasonDTO);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/list-request-application")
    public ResponseEntity<List<RequestApplicationDTO>> getRequestApplicationByReason(
            @RequestParam(name = "reason",value = "") String reason) throws IOException {
        return ResponseEntity.ok(requestApplicationService.getRequestApplicationByReason(reason));
    }

    @PutMapping("/list-request-application")
    public ResponseEntity<HttpStatus> updateRequestApplication(@RequestBody Map<String,String> map){
        try {
            requestApplicationService.updateReason(map);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
