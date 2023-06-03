package com.swef.cookcode.common.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UrlResponse {

    List<String> urls;
}
