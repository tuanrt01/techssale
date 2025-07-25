package com.techzen.techsale.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashBoardDTO {
    private String reason;
    private String user;
    private Integer quantity;
    private Integer month;
    private Integer year;
    private Integer quarter;
    private Long total;
}
