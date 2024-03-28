package com.study.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SearchBookReqDto {
    private int count;
    private int page;
    private int bookTypeId;
    private int categoryId;
    private int searchTypeId;
    private String searchText;
}
