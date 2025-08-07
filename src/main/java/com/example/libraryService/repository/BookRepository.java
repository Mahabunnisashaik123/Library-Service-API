package com.example.libraryService.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.libraryService.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Optional is used to avoid null
	List<Book> findByTitleContainingIgnoreCase(String title);
	List<Book> findByAuthorContainingIgnoreCase(String author);
	List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);

}
