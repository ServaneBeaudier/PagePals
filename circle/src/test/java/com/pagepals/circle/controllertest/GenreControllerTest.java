package com.pagepals.circle.controllertest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.pagepals.circle.controller.GenreController;
import com.pagepals.circle.model.LiteraryGenre;
import com.pagepals.circle.repository.LiteraryGenreRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {

    @Mock
    private LiteraryGenreRepository genreRepository;

    @InjectMocks
    private GenreController genreController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(genreController).build();
    }

    @Test
    void getGenres_returnsListOfMapsAndStatusOk() throws Exception {
        LiteraryGenre genre1 = new LiteraryGenre(1L, "Fantastique");
        LiteraryGenre genre2 = new LiteraryGenre(2L, "Science-Fiction");

        when(genreRepository.findAll()).thenReturn(List.of(genre1, genre2));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nom").value("Fantastique"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nom").value("Science-Fiction"));

        verify(genreRepository).findAll();
    }
}
