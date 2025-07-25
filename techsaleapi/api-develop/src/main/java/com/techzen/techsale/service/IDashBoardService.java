package com.techzen.techsale.service;

import com.techzen.techsale.dto.dashboard.DashBoardDTO;
import com.techzen.techsale.dto.dashboard.IDashBoardDTO;
import com.techzen.techsale.dto.response.ExpenseDetailDTO;

import java.util.List;

public interface IDashBoardService {

    Long getTotalPriceInYear(Integer year);

    List<IDashBoardDTO> getReasonBest(Integer year);

    List<IDashBoardDTO> getReasonBestByMonth(Integer year, Integer month);

    List<IDashBoardDTO> getMonthBest(Integer year);

    List<IDashBoardDTO> getUserBest(Integer year);

    List<IDashBoardDTO> getUserBestByMonth(Integer year, Integer month);

    DashBoardDTO getHeader(Integer year);

    List<DashBoardDTO> getTotalByMonth(Integer year);

    List<DashBoardDTO> getTotalByQuarter(Integer year);

    List<ExpenseDetailDTO> getExpenseDetailByMonth(Integer year, Integer month);
    
    List<ExpenseDetailDTO> getExpenseDetailByQuarter(Integer year, Integer quarter);
    
    List<ExpenseDetailDTO> getExpenseDetailByReason(Integer year, String reason);

    List<ExpenseDetailDTO> getExpenseDetailByUser(Integer year, String userId);
}
