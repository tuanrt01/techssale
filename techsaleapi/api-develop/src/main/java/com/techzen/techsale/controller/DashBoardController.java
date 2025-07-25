package com.techzen.techsale.controller;

import com.techzen.techsale.dto.dashboard.DashBoardDTO;
import com.techzen.techsale.dto.dashboard.IDashBoardDTO;
import com.techzen.techsale.dto.response.ExpenseDetailDTO;
import com.techzen.techsale.service.IDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/dash-board")
public class DashBoardController {

    private final IDashBoardService dashBoardService;

    @GetMapping("/header")
    public ResponseEntity<DashBoardDTO> getAllHeader(@RequestParam(name = "year",defaultValue = "2024") String year){
        return ok(dashBoardService.getHeader(Integer.parseInt(year)));
    }

    @GetMapping("/total-by-month")
    public ResponseEntity<List<DashBoardDTO>> getTotalByMonth(@RequestParam(name = "year",defaultValue = "2024") String year){
        return ok(dashBoardService.getTotalByMonth(Integer.parseInt(year)).stream()
                .sorted(Comparator.comparingInt(DashBoardDTO::getMonth))
                .collect(Collectors.toList()));
    }

    @GetMapping("/total-by-quarter")
    public ResponseEntity<List<DashBoardDTO>> getTotalByQuarter(@RequestParam(name = "year",defaultValue = "2024") String year){
        return ok(dashBoardService.getTotalByQuarter(Integer.parseInt(year)));
    }

    @GetMapping("/total-by-reason")
    public ResponseEntity<List<IDashBoardDTO>> getReason(
            @RequestParam(name = "year", defaultValue = "2024") String year,
            @RequestParam(name = "month", required = false) Integer month) {
        if (month != null) {
            return ok(dashBoardService.getReasonBestByMonth(Integer.parseInt(year), month));
        }
        return ok(dashBoardService.getReasonBest(Integer.parseInt(year)));
    }

    @GetMapping("/total-by-user")
    public ResponseEntity<List<IDashBoardDTO>> getBestUser(
            @RequestParam(name = "year", defaultValue = "2024") String year,
            @RequestParam(name = "month", required = false) Integer month) {
        if (month != null) {
            return ok(dashBoardService.getUserBestByMonth(Integer.parseInt(year), month));
        }
        return ok(dashBoardService.getUserBest(Integer.parseInt(year)));
    }

    @GetMapping("/month-detail")
    public ResponseEntity<List<ExpenseDetailDTO>> getMonthDetail(@RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(dashBoardService.getExpenseDetailByMonth(year, month));
    }

    @GetMapping("/quarter-detail")
    public ResponseEntity<List<ExpenseDetailDTO>> getQuarterDetail(@RequestParam Integer year, @RequestParam Integer quarter) {
        return ResponseEntity.ok(dashBoardService.getExpenseDetailByQuarter(year, quarter));
    }

    @GetMapping("/reason-detail")
    public ResponseEntity<List<ExpenseDetailDTO>> getReasonDetail(@RequestParam Integer year, @RequestParam String reason) {
        return ResponseEntity.ok(dashBoardService.getExpenseDetailByReason(year, reason));
    }

    @GetMapping("/user-detail")
    public ResponseEntity<List<ExpenseDetailDTO>> getUserDetail(@RequestParam Integer year, @RequestParam String user) {
        return ResponseEntity.ok(dashBoardService.getExpenseDetailByUser(year, user));
    }
}
