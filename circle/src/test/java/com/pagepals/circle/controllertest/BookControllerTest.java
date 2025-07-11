package com.pagepals.circle.controllertest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagepals.circle.controller.BookController;
import com.pagepals.circle.dto.*;
import com.pagepals.circle.service.BookSearchService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookSearchService bookSearchService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void searchBook_returnsListAndStatusOk() throws Exception {
        SearchBookDTO searchDTO = new SearchBookDTO();
        searchDTO.setCritereRecherche("Java");

        BookDTO bookDTO = BookDTO.builder()
                .titre("Effective Java")
                .isbn("1234567890")
                .build();

        when(bookSearchService.searchBooks("Java")).thenReturn(List.of(bookDTO));

        mockMvc.perform(post("/api/books/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(searchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").value("Effective Java"))
                .andExpect(jsonPath("$[0].isbn").value("1234567890"));

        verify(bookSearchService).searchBooks("Java");
    }

    // Méthode utilitaire JSON (copier/coller)
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
