package com.infrastructure.common;

import java.util.Collection;

public interface PageResponse<T> {

    Collection<T> getResult();
}
