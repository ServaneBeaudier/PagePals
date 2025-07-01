package com.pagepals.circle.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteriaDTO {

    private String motCle;

    private String genre;

    private String format;

    private LocalDate date;
}
