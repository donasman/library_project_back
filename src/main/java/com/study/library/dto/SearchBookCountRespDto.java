package com.study.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchBookCountRespDto {
    private int totalCount;
    private int maxPageNumber;

}
