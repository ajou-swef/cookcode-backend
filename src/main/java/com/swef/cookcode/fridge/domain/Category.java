package com.swef.cookcode.fridge.domain;

import com.swef.cookcode.common.error.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;

import static com.swef.cookcode.common.ErrorCode.CATEGORY_NOT_FOUND;

@Getter
public enum Category {
    MEAT("1","육류"),
    SEAFOOD("2","해산물"),
    MILK("3","유제품"),
    GRAIN("4","곡물"),
    VEGETABLE("5","채소"),
    FRUIT("6","과일"),
    SEASONING("7","양념");

    final private String code;
    final private String name;

    Category(String code, String name){
        this.code = code;
        this.name = name;
    }

    public static Category ofCode(String code){
        return Arrays.stream(Category.values())
                .filter(c -> c.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
    }
}
