package com.infrastructure.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageResponse<T> {

    public List<T> result = new ArrayList();

    private Long totalRows=0L;
}
