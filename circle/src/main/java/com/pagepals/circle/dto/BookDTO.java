package com.pagepals.circle.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookDTO {

    private String titre;

    private List<String> auteurs;

    private String isbn;

    private String genre;

    private String couvertureUrl;
}
