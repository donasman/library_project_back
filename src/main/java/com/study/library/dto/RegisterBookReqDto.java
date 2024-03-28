package com.study.library.dto;

import com.study.library.entity.Book;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterBookReqDto {

    private String isbn;
    @Min(value = 1, message = "숫자만 입력 가능합니다.") // @Min 사용하려면 자료형이 int 정수만 가능
    private int bookTypeId;
    @Min(value = 1, message = "숫자만 입력 가능합니다.")
    private int categoryId;
    @NotBlank(message = "도서명은 빈 값일 수 없습니다.") // 공백, null, 띄어쓰기 체크를 동시에 함
    private String bookName;
    // @NotNull null 체크
    // @Null null만 가능
    // @Empty 공백만 가능 NUll(x)
    @NotBlank(message = "저자명은 빈 값일 수 없습니다.")
    private String authorName;
    @NotBlank(message = "출판사명은 빈 값일 수 없습니다.")
    private String publisherName;
    @NotBlank(message = "이미지 주소는 빈 값일 수 없습니다.")
    private String coverImgUrl;

    public Book toEntity() {
        return Book.builder()
                .bookName(bookName)
                .authorName(authorName)
                .publisherName(publisherName)
                .bookTypeId(bookTypeId)
                .categoryId(categoryId)
                .isbn(isbn)
                .coverImgUrl(coverImgUrl)
                .build();
    }
}
