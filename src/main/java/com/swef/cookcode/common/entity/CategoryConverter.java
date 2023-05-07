package com.swef.cookcode.common.entity;

import com.swef.cookcode.fridge.domain.Category;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CategoryConverter implements AttributeConverter<Category, String> {

    @Override
    public String convertToDatabaseColumn(Category attribute) {
        return attribute.getCode();
    }

    @Override
    public Category convertToEntityAttribute(String dbData) {
        return Category.ofCode(dbData);
    }
}
