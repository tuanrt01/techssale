package com.techzen.techsale.service.impl;

import com.techzen.techsale.dto.dashboard.DashBoardDTO;
import com.techzen.techsale.dto.dashboard.IDashBoardDTO;
import com.techzen.techsale.dto.response.ExpenseDetailDTO;
import com.techzen.techsale.repository.IDashBoardRepository;
import com.techzen.techsale.service.IDashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashBoardService implements IDashBoardService {

    private final IDashBoardRepository dashBoardRepository;

    @Override
    public Long getTotalPriceInYear(Integer year) {
        return dashBoardRepository.getTotalPriceInYear(year);
    }

    @Override
    public List<IDashBoardDTO> getReasonBest(Integer year) {
        return dashBoardRepository.getReasonBest(year);
    }

    @Override
    public List<IDashBoardDTO> getReasonBestByMonth(Integer year, Integer month) {
        return dashBoardRepository.getReasonBestByMonth(year, month);
    }

    @Override
    public List<IDashBoardDTO> getMonthBest(Integer year) {
        return dashBoardRepository.getMonthBest(year);
    }

    @Override
    public List<IDashBoardDTO> getUserBest(Integer year) {
        return dashBoardRepository.getUserBest(year);
    }

    @Override
    public List<IDashBoardDTO> getUserBestByMonth(Integer year, Integer month) {
        return dashBoardRepository.getUserBestByMonth(year, month);
    }

    @Override
    public DashBoardDTO getHeader(Integer year) {
        try {
            DashBoardDTO dashBoardDTO = new DashBoardDTO();
            dashBoardDTO.setMonth(getMonthBest(year).get(0).getMonth());
            dashBoardDTO.setYear(getMonthBest(year).get(0).getYear());
            dashBoardDTO.setReason(getReasonBest(year).get(0).getReason());
            dashBoardDTO.setTotal(getTotalPriceInYear(year));
            dashBoardDTO.setUser(getUserBest(year).get(0).getUser());
            return dashBoardDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<DashBoardDTO> getTotalByMonth(Integer year) {
        List<DashBoardDTO> boardDTOList = new ArrayList<>();
        List<IDashBoardDTO> list = dashBoardRepository.getMonthBest(year);
        DashBoardDTO dashBoardTemp;
        boolean flag = true;
        for (int i = 1; i <= 12; i++) {
            dashBoardTemp = new DashBoardDTO();
            for (IDashBoardDTO dashBoardDTO : list) {
                if (dashBoardDTO.getMonth() == i) {
                    BeanUtils.copyProperties(dashBoardDTO, dashBoardTemp);
                    boardDTOList.add(dashBoardTemp);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                dashBoardTemp.setMonth(i);
                dashBoardTemp.setQuantity(0);
                dashBoardTemp.setTotal(0L);
                boardDTOList.add(dashBoardTemp);
            }
        }
        return boardDTOList;
    }

    @Override
    public List<DashBoardDTO> getTotalByQuarter(Integer year) {
        List<DashBoardDTO> boardDTOList = new ArrayList<>();
        List<DashBoardDTO> list = getTotalByMonth(year);
        long sum = 0;
        int month = 0;
        int quarter = 1;
        DashBoardDTO dashBoardDTO;
        for (int i = 0; i < 4; i++) {
            dashBoardDTO = new DashBoardDTO();
            for (int j = 0; j < 3; j++) {
                if (j < list.size() && month < list.size()) {
                    sum += list.get(month++).getTotal();
                } else {
                    sum += 0;
                }
            }
            dashBoardDTO.setQuarter(quarter++);
            dashBoardDTO.setTotal(sum);
            sum = 0;
            boardDTOList.add(dashBoardDTO);
        }
        return boardDTOList;
    }

    @Override
    public List<ExpenseDetailDTO> getExpenseDetailByMonth(Integer year, Integer month) {
        List<Object[]> resultList = dashBoardRepository.findExpenseDetailsByMonth(year, month);
        return mapToExpenseDetailDTO(resultList);
    }

    @Override
    public List<ExpenseDetailDTO> getExpenseDetailByQuarter(Integer year, Integer quarter) {
        List<Object[]> resultList = dashBoardRepository.findExpenseDetailsByQuarter(year, quarter);
        return mapToExpenseDetailDTO(resultList);
    }

    @Override
    public List<ExpenseDetailDTO> getExpenseDetailByReason(Integer year, String reason) {
        List<Object[]> resultList = dashBoardRepository.findExpenseDetailsByReason(year, reason);
        return mapToExpenseDetailDTO(resultList);
    }

    @Override
    public List<ExpenseDetailDTO> getExpenseDetailByUser(Integer year, String userId) {
        List<Object[]> resultList = dashBoardRepository.findExpenseDetailsByUser(year, userId);
        return mapToExpenseDetailDTO(resultList);
    }

    private List<ExpenseDetailDTO> mapToExpenseDetailDTO(List<Object[]> resultList) {
        List<ExpenseDetailDTO> detailDTOs = new ArrayList<>();
        for (Object[] result : resultList) {
            try {
                String user = (String) result[0];
                String product = (String) result[1];
                BigDecimal amount = new BigDecimal(result[2].toString());
                LocalDateTime date = null;
                if (result[3] instanceof java.sql.Timestamp) {
                    date = ((java.sql.Timestamp) result[3]).toLocalDateTime();
                } else if (result[3] instanceof LocalDateTime) {
                    date = (LocalDateTime) result[3];
                } else if (result[3] instanceof java.util.Date) {
                    date = ((java.util.Date) result[3]).toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
                } else if (result[3] != null) {
                    try {
                        date = java.time.LocalDateTime.parse(result[3].toString());
                    } catch (Exception e) {
                        date = LocalDateTime.now();
                    }
                }
                detailDTOs.add(ExpenseDetailDTO.builder()
                        .user(user)
                        .product(product)
                        .amount(amount)
                        .date(date)
                        .build());
            } catch (Exception e) {
            }
        }
        
        return detailDTOs;
    }
}
