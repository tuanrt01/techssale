package com.techzen.techsale.dto.response;

import com.techzen.techsale.dto.RequestApplicationForListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestApplicationForListResponse {
    private List<RequestApplicationForListDTO> content;
    private Long totalElements;
    private Long totalPages;
    private Integer page;
    private Integer size;
}
