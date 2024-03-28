package com.study.library.entity;

import com.study.library.dto.SearchBookReqDto;
import com.study.library.dto.SearchBooksRespDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {
    private int bookId;
    private String bookName;
    private String authorName;
    private String publisherName;
    private String isbn;
    private int bookTypeId;
    private int categoryId;
    private String coverImgUrl;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private BookType bookType;
    private Category category;

    public SearchBooksRespDto toSearchBookReqDto() {
        return SearchBooksRespDto.builder()
                .bookId(bookId)
                .bookName(bookName)
                .authorName(authorName)
                .publisherName(publisherName)
                .isbn(isbn)
                .bookTypeId(bookTypeId)
                .bookTypeName(bookType.getBookTypeName())
                .categoryId(categoryId)
                .categoryName(category.getCategoryName())
                .coverImgUrl(coverImgUrl)
                .build();
    }
}
