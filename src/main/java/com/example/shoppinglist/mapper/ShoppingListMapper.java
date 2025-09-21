package com.example.shoppinglist.mapper;

import com.example.shoppinglist.entity.ShoppingList;
import com.example.shoppinglist.model.ShoppingListResponse;

public class ShoppingListMapper {

    public static ShoppingListResponse toResponse(ShoppingList entity) {

        if (entity == null) {
            return null;
        }

        return new ShoppingListResponse(
                entity.getId(),
                entity.getName(),
                entity.getItems() != null ? entity.getItems().stream().map(ShoppingListItemMapper::toResponse).toList() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
