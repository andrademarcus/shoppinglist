package com.example.shoppinglist.mapper;

import com.example.shoppinglist.entity.Item;
import com.example.shoppinglist.model.ItemResponse;

public class ItemMapper {

    public static ItemResponse toResponse(Item item) {

        if (item == null) {
            return null;
        }

        return new ItemResponse(
                item.getId(),
                item.getCategory() != null ? CategoryMapper.toResponse(item.getCategory()) : null,
                item.getName(),
                item.getPrice()
        );

    }

}
