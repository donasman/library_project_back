package com.study.library.controller.admin;

import com.study.library.aop.annotation.ParamsPrint;
import com.study.library.aop.annotation.ValidAspect;
import com.study.library.dto.RegisterBookReqDto;
import com.study.library.dto.SearchBookReqDto;
import com.study.library.dto.UpdateBookReqDto;
import com.study.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @ValidAspect
    @PostMapping("/book")
    public ResponseEntity<?> saveBook(@Valid @RequestBody RegisterBookReqDto registerBookReqDto, BindingResult bindingResult) {

        bookService.saveBook(registerBookReqDto);

        return ResponseEntity.created(null).body(true);
    }

    @ParamsPrint
    @GetMapping("/books")
    public ResponseEntity<?> searchBooks(SearchBookReqDto searchBookReqDto) {

        return ResponseEntity.ok(bookService.searchBooks(searchBookReqDto));
    }

    @GetMapping("/books/count")
    public ResponseEntity<?> getCount(SearchBookReqDto searchBookReqDto) {

        return ResponseEntity.ok(bookService.getBookCount(searchBookReqDto));
    }

    @DeleteMapping("/book/{bookId}") //단건 삭제
    public ResponseEntity<?> deleteBooks(@PathVariable int bookId) {

        return ResponseEntity.ok(null);
    }

    @ParamsPrint
    @DeleteMapping("/books") // 다중 삭제
    public ResponseEntity<?> deleteBooks(@RequestBody List<Integer> bookIds) {
        bookService.deleteBooks(bookIds);

        return ResponseEntity.ok(true);
    }

    @PutMapping("/book/{bookId}")
    public ResponseEntity<?> updateBook (
            @PathVariable int bookId,
            @RequestBody UpdateBookReqDto updateBookReqDto) {

        bookService.updateBook(updateBookReqDto);

        return ResponseEntity.ok(true);
    }


}
