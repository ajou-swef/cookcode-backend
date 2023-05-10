package com.swef.cookcode.fridge.domain;

import com.swef.cookcode.common.error.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;

import static com.swef.cookcode.common.ErrorCode.CATEGORY_NOT_FOUND;

@Getter
public enum Category {
    MEAT("육류"),
    SEAFOOD("해산물"),
    MILK("유제품"),
    GRAIN("곡물"),
    VEGETABLE("채소"),
    FRUIT("과일"),
    SEASONING("양념");

    final private String name;

    Category(String name){
        this.name = name;
    }

    public static Category ofName(String name){
        return Arrays.stream(Category.values())
                .filter(c -> c.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
    }
}
