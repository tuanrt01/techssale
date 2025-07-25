package com.techzen.techsale.dto.request;

import com.techzen.techsale.enumeration.RequestStatusEnum;
import com.techzen.techsale.enumeration.SortFieldRequestEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private int size = 10;

    private SortFieldRequestEnum sort;

    private int page = 0;

    private Sort.Direction direction;

    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtBegin;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAtEnd;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expectReceiveDateBegin;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expectReceiveDateEnd;

    private List<RequestStatusEnum> statusList;

    private String requestReason;

}
