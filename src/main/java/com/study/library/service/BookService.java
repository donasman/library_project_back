package com.study.library.service;

import com.study.library.dto.*;
import com.study.library.entity.Book;
import com.study.library.repository.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    @Transactional(rollbackFor = Exception.class) // insert update delete 예외 발생 시 rollback 걸어둔다.
    public void saveBook(RegisterBookReqDto registerBookReqDto) {
        bookMapper.saveBook(registerBookReqDto.toEntity());
    }

    public List<SearchBooksRespDto> searchBooks(SearchBookReqDto searchBookReqDto) {
        int startIndex = (searchBookReqDto.getPage() - 1) * searchBookReqDto.getCount();

        List<Book> books = bookMapper.findBooks(
                startIndex,
                searchBookReqDto.getCount(),
                searchBookReqDto.getBookTypeId(),
                searchBookReqDto.getCategoryId(),
                searchBookReqDto.getSearchTypeId(),
                searchBookReqDto.getSearchText()
        );

        return books.stream().map(Book::toSearchBookReqDto).collect(Collectors.toList());
    }
    public SearchBookCountRespDto getBookCount(SearchBookReqDto searchBookReqDto) {
        int bookCount = bookMapper.getBookCount(
                searchBookReqDto.getSearchTypeId(),
                searchBookReqDto.getCategoryId(),
                searchBookReqDto.getSearchTypeId(),
                searchBookReqDto.getSearchText()
        );
        int maxPageNumber = (int) Math.ceil(((double) bookCount) / searchBookReqDto.getCount());

        return SearchBookCountRespDto.builder()
                .totalCount(bookCount)
                .maxPageNumber(maxPageNumber)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBooks(List<Integer> bookIds) {
        bookMapper.deleteBooksByBookIds(bookIds);

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBook(UpdateBookReqDto updateBookReqDto) {

        bookMapper.updateBookByBookId(updateBookReqDto.toEntity());
    }
}
