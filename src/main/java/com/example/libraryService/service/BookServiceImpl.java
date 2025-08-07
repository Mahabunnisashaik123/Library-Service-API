package com.example.libraryService.service;

import com.example.libraryService.dto.BookRequestDTO;
import com.example.libraryService.dto.BookResponseDTO;
import com.example.libraryService.entity.Book;
import com.example.libraryService.exception.ResourceNotFoundException;
import com.example.libraryService.kafka.BookKafkaEvent;
import com.example.libraryService.kafka.LibraryKafkaProducer;
import com.example.libraryService.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryKafkaProducer kafkaProducer;

    @Autowired
    private EmailService emailService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public BookResponseDTO createBook(BookRequestDTO dto) {
        log.info("Creating book with title: {}", dto.getTitle());

        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .email(dto.getEmail())
                .build();

        Book savedBook = bookRepository.save(book);
        log.debug("Book saved with ID: {}", savedBook.getId());

        publishKafkaEvents(savedBook, "CREATE");

        if (savedBook.getEmail() != null && !savedBook.getEmail().isBlank()) {
            try {
                String subject = "Book Created: " + savedBook.getTitle();
                String body = "The book \"" + savedBook.getTitle() + "\" has been added to the library successfully.";

                Map<String, Object> model = new HashMap<>();
                model.put("name", dto.getRecipientName());
                model.put("bookTitle", savedBook.getTitle());

                emailService.sendTemplateMail(
                        savedBook.getEmail(),
                        subject,
                        dto.getTemplateType(),
                        body,
                        model
                );

                log.info("Email sent successfully to {}", savedBook.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage(), e);
            }
        }

        return mapToResponse(savedBook);
    }

    @Override
    public BookResponseDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }
    
    @Override
    public List<BookResponseDTO> searchBooks(Optional<String> title, Optional<String> author) {
        List<Book> books;

        if (title.isPresent() && author.isPresent()) {
            books = bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
                    title.get(), author.get());
        } else if (title.isPresent()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title.get());
        } else if (author.isPresent()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author.get());
        } else {
            books = Collections.emptyList();
        }

        return books.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());

        Book updated = bookRepository.save(existing);
        publishKafkaEvents(updated, "UPDATE");

        if (existing.getEmail() != null && !existing.getEmail().isBlank()) {
            try {
                String subject = "Book Updated: " + existing.getTitle();
                String body = "The book \"" + existing.getTitle() + "\" has been updated in the library.";
                emailService.sendSimpleMail(existing.getEmail(), subject, body);
                log.info("Email sent successfully after update to {}", existing.getEmail());
            } catch (Exception e) {
                log.error("Failed to send update email: {}", e.getMessage(), e);
            }
        }

        return mapToResponse(updated);
    }

    @Override
    public BookResponseDTO patchBook(Long id, BookRequestDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        Optional.ofNullable(dto.getTitle()).ifPresent(book::setTitle);
        Optional.ofNullable(dto.getAuthor()).ifPresent(book::setAuthor);
        Optional.ofNullable(dto.getPrice()).ifPresent(book::setPrice);
        if (dto.getStock() > 0) book.setStock(dto.getStock());

        Book patched = bookRepository.save(book);
        publishKafkaEvents(patched, "PATCH");

        if (book.getEmail() != null && !book.getEmail().isBlank()) {
            try {
                String subject = "Book Patched: " + book.getTitle();
                String body = "The book \"" + book.getTitle() + "\" has been patched successfully.";
                emailService.sendSimpleMail(book.getEmail(), subject, body);
                log.info("Patch email sent to {}", book.getEmail());
            } catch (Exception e) {
                log.error("Failed to send patch email: {}", e.getMessage(), e);
            }
        }

        return mapToResponse(patched);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        bookRepository.delete(book);
        publishKafkaEvents(book, "DELETE");

        if (book.getEmail() != null && !book.getEmail().isBlank()) {
            try {
                String subject = "Book Deleted: " + book.getTitle();
                String body = "The book \"" + book.getTitle() + "\" has been deleted from the library.";
                emailService.sendSimpleMail(book.getEmail(), subject, body);
                log.info("Delete email sent to {}", book.getEmail());
            } catch (Exception e) {
                log.error("Failed to send delete email: {}", e.getMessage(), e);
            }
        }
    }

    private void publishKafkaEvents(Book book, String actionType) {
        try {
            String message = actionType + " Book: " + book.getTitle();
            kafkaProducer.sendStringEvent(message);

            BookKafkaEvent event = BookKafkaEvent.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .price(book.getPrice())
                    .stock(book.getStock())
                    .action(actionType)
                    .timestamp(formatter.format(LocalDateTime.now()))
                    .build();

            kafkaProducer.sendJsonEvent(event);
            log.info("Kafka events published for {} action on book: {}", actionType, book.getTitle());
        } catch (Exception e) {
            log.error("Failed to publish Kafka events: {}", e.getMessage(), e);
        }
    }

    private BookResponseDTO mapToResponse(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .stock(book.getStock())
                .build();
    }
}
