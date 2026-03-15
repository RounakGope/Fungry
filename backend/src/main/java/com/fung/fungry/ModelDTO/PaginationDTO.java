package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PaginationDTO {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
