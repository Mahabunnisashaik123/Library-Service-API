package com.example.libraryService.controller;

import com.example.libraryService.dto.BookRequestDTO;
import com.example.libraryService.dto.BookResponseDTO;
import com.example.libraryService.response.ApiCode;
import com.example.libraryService.response.ApiResponse;
import com.example.libraryService.service.BookService;
import com.example.libraryService.service.InventoryClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * ============================================================================
 * Controller Class: BookController
 * 
 * Description:
 *   Exposes RESTful endpoints for managing book entities within the library.
 *   Supports full CRUD operations and inventory integration via Resilience4j.
 * 
 * Base Path: /api/books
 * ============================================================================
 */

@Slf4j
@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Controller", description = "Library API for book operations")
public class BookController {

	 /** Service responsible for book operations */
    @Autowired
    private BookService bookService;

    /** Client service for communicating with Inventory microservice. */
    @Autowired
    private InventoryClientService inventoryClientService;

    // -----------------------------------------------------------
    // POST: Create a new book
    // -----------------------------------------------------------

    /**
     * Creates a new book entry in the library.
     *
     * @param requestDTO DTO containing book details to create.
     * @return ApiResponse with created book details.
     */
    @PostMapping
    @Operation(
        summary = "Create a new book",
        description = "Creates a new book entry in the library with the given details.",
        requestBody = @RequestBody(
            required = true,
            description = "Book request payload",
            content = @Content(schema = @Schema(implementation = BookRequestDTO.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or validation error", content = @Content)
        }
    )
    public ApiResponse<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO requestDTO) {
        log.info("📘 Creating book: {}", requestDTO.getTitle());
        BookResponseDTO created = bookService.createBook(requestDTO);
        return ApiResponse.<BookResponseDTO>builder()
                .status(ApiCode.CREATED.getStatusCode())
                .message("Book created successfully")
                .data(created)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // -----------------------------------------------------------
    // GET: Retrieve all books
    // -----------------------------------------------------------

    /**
     * Retrieves all book entries.
     *
     * @return ApiResponse containing list of all books.
     */
    @GetMapping
    @Operation(
        summary = "Get all books",
        description = "Fetches a list of all books stored in the library.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of all books")
        }
    )
    public ApiResponse<List<BookResponseDTO>> getAllBooks() {
        log.debug("📚 Getting all books");
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ApiResponse.of(ApiCode.SUCCESS, books);
    }


    // -----------------------------------------------------------
    // GET: Retrieve book by ID
    // -----------------------------------------------------------
    
    

    /**
     * Retrieves a book by its ID.
     *
     * @param id ID of the book to fetch.
     * @return ApiResponse containing the book details.
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get book by ID",
        description = "Retrieves a single book based on its unique ID.",
        parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Book ID")
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book found and returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
        }
    )
    public ApiResponse<BookResponseDTO> getBookById(@PathVariable Long id) {
        log.debug("🔍 Getting book by ID: {}", id);
        return ApiResponse.of(ApiCode.SUCCESS, bookService.getBookById(id));
    }

 // ============================================================================
    // PUT: Full update of a book
    // ============================================================================

    /**
     * Fully updates a book record by ID.
     *
     * @param id  Book ID to update.
     * @param dto New book data.
     * @return ApiResponse with updated book.
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update entire book by ID",
        description = "Updates all fields of a book based on the given ID.",
        parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Book ID")
        },
        requestBody = @RequestBody(
            required = true,
            description = "Updated book information",
            content = @Content(schema = @Schema(implementation = BookRequestDTO.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
        }
    )
    public ApiResponse<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO dto) {
        log.info("✏️ Updating book ID: {}", id);
        return ApiResponse.of(ApiCode.UPDATED, bookService.updateBook(id, dto));
    }

 // ============================================================================
    // PATCH: Partial update of a book
    // ============================================================================

    /**
     * Partially updates book fields.
     *
     * @param id  Book ID.
     * @param dto Partial book data.
     * @return ApiResponse with patched book.
     */
    @PatchMapping("/{id}")
    @Operation(
        summary = "Partially update book",
        description = "Updates one or more fields of a book based on its ID.",
        parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Book ID")
        },
        requestBody = @RequestBody(
            required = true,
            description = "Partial book data",
            content = @Content(schema = @Schema(implementation = BookRequestDTO.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
        }
    )
    public ApiResponse<BookResponseDTO> patchBook(@PathVariable Long id, @RequestBody BookRequestDTO dto) {
        log.info("🩹 Patching book ID: {}", id);
        return ApiResponse.of(ApiCode.UPDATED, bookService.patchBook(id, dto));
    }

    // ============================================================================
    // DELETE: Delete a book by ID
    // ============================================================================

    /**
     * Deletes a book from the system.
     *
     * @param id ID of the book to delete.
     * @return ApiResponse confirming deletion.
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete book by ID",
        description = "Deletes a book from the library based on its ID.",
        parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Book ID")
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
        }
    )
    public ApiResponse<Object> deleteBook(@PathVariable Long id) {
        log.warn("🗑️ Deleting book ID: {}", id);
        bookService.deleteBook(id);
        return ApiResponse.of(ApiCode.DELETED, "Book deleted successfully");
    }

    // ============================================================================
    // GET: Inventory products via Resilience4j
    // ============================================================================

    /**
     * Retrieves inventory product data from InventoryService.
     *
     * @return CompletableFuture with inventory response.
     */ 
    @GetMapping("/inventory-products")
    @Operation(
        summary = "Get inventory products from InventoryService",
        description = "Fetches product data from InventoryService using Resilience4j CircuitBreaker, Retry, and TimeLimiter.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventory data fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Inventory service is unavailable or failed")
        }
    )
    public CompletableFuture<ApiResponse<String>> getInventoryProducts() {
        log.info("📦 Requesting inventory products via Resilience4j...");
        return inventoryClientService.getAllProducts()
                .thenApply(data -> ApiResponse.of(ApiCode.SUCCESS, data));
    }
    
    // ============================================================================
    // GET: Search books by title and/or author
    // ============================================================================

    /**
     * Searches books by optional title and author.
     *
     * @param title  Optional title filter.
     * @param author Optional author filter.
     * @return ApiResponse with filtered book list.
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search books by optional title and/or author",
        description = "Fetches books matching the optional title and author query parameters",
        parameters = {
            @Parameter(name = "title", in = ParameterIn.QUERY, required = false, description = "Book title (optional)"),
            @Parameter(name = "author", in = ParameterIn.QUERY, required = false, description = "Book author (optional)")
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books fetched successfully")
        }
    )
    public ApiResponse<List<BookResponseDTO>> searchBooks(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {

        log.info("🔎 Searching books by title='{}', author='{}'", title.orElse(""), author.orElse(""));
        List<BookResponseDTO> result = bookService.searchBooks(title, author);
        return ApiResponse.of(ApiCode.SUCCESS, result);
    }
    

} 
