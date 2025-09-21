package com.example.shoppinglist.mapper;

import com.example.shoppinglist.entity.ShoppingListItem;
import com.example.shoppinglist.model.ShoppingListItemResponse;

public class ShoppingListItemMapper {

    public static ShoppingListItemResponse toResponse(ShoppingListItem entity) {

        if (entity == null) {
            return null;
        }

        return new ShoppingListItemResponse(
                entity.getItem() != null ? ItemMapper.toResponse(entity.getItem()) : null,
                entity.getQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
