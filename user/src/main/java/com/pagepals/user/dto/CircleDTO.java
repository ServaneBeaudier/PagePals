package com.pagepals.user.dto;

import java.util.List;

import lombok.Data;

@Data
public class CircleDTO {

    private long id;

    private Long createurId;

    private List<String> genres;
    private List<Long> genreIds;
}
