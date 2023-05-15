package com.swef.cookcode.common;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UrlResponse {

    List<String> urls;
}
