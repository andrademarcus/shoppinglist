package com.example.shoppinglist.mapper;

import com.example.shoppinglist.entity.Category;
import com.example.shoppinglist.model.CategoryResponse;

public class CategoryMapper {

    public static CategoryResponse toResponse(Category category) {

        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );

    }

}
