package com.example.libraryService.service;
import java.util.List;
import java.util.Optional;

import com.example.libraryService.dto.BookRequestDTO;
import com.example.libraryService.dto.BookResponseDTO;

public interface BookService {

    BookResponseDTO createBook(BookRequestDTO requestDTO);

    BookResponseDTO getBookById(Long id);

    List<BookResponseDTO> getAllBooks();

    BookResponseDTO updateBook(Long id, BookRequestDTO requestDTO);

    BookResponseDTO patchBook(Long id, BookRequestDTO requestDTO);
    
    List<BookResponseDTO> searchBooks(Optional<String> title, Optional<String> author);

    void deleteBook(Long id);
    
}
