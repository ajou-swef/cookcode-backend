package com.swef.cookcode.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {
  private final List<T> content;
  private final int numberOfElements;
  private final boolean hasNext;

  @Builder
  public SliceResponse(Slice<T> slice) {
    this.content = slice.getContent();
    this.numberOfElements = slice.getNumberOfElements();
    this.hasNext = slice.hasNext();
  }
}
