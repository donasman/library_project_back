package com.study.library.dto;

import com.study.library.entity.Book;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateBookReqDto {

    private int bookId;
    private String bookName;
    private String authorName;
    private String publisherName;
    private String isbn;
    private int categoryId;
    private int bookTypeId;
    private String coverImgUrl;

    public Book toEntity() {
        return Book.builder()
                .bookId(bookId)
                .bookName(bookName)
                .authorName(authorName)
                .publisherName(publisherName)
                .isbn(isbn)
                .categoryId(categoryId)
                .bookTypeId(bookTypeId)
                .coverImgUrl(coverImgUrl)
                .build();
    }
}
