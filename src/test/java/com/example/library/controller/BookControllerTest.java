package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldAddNewBook() throws Exception {
        Book book = new Book(1, "Spring Boot in Action", "Craig Walls", 2016);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                 
                .andExpect(jsonPath("$.title").value("Spring Boot in Action"))
                .andExpect(jsonPath("$.author").value("Craig Walls"))
                .andExpect(jsonPath("$.year").value(2016));
    }

    @Test
    void shouldRetrieveAllBooks() throws Exception {
        Book book1 = new Book(1, "Spring Boot in Action", "Craig Walls", 2016);
        Book book2 = new Book(2, "Spring Boot Up & Running", "Mark Heckler", 2021);

        bookRepository.save(book1);
        bookRepository.save(book2);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot in Action"))
                .andExpect(jsonPath("$[1].title").value("Spring Boot Up & Running"));
    }

    @Test
    void shouldUpdateExistingBook() throws Exception {
        Book book = new Book(1, "Spring Boot in Action", "Craig Walls", 2016);
        bookRepository.save(book);

        Book updatedBook = new Book(1, "Spring Boot Up & Running", "Mark Heckler", 2021);

        mockMvc.perform(put("/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Up & Running"))
                .andExpect(jsonPath("$.author").value("Mark Heckler"))
                .andExpect(jsonPath("$.year").value(2021));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        Book book = new Book(1, "Spring Boot in Action", "Craig Walls", 2016);
        bookRepository.save(book);

        mockMvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/{id}", book.getId()))
                .andExpect(status().isNotFound());
    }
}
