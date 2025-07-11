package com.pagepals.circle.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private String titre;

    private List<String> auteurs;

    private String isbn;

    private String genre;

    private String couvertureUrl;
}
