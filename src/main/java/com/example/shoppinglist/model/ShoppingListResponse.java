package com.example.shoppinglist.model;

import com.example.shoppinglist.entity.ShoppingListItem;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

public record ShoppingListResponse(Long id,
                                   String name,
                                   List<ShoppingListItemResponse> items,

                                   @JsonFormat(shape = JsonFormat.Shape.STRING)
                                   Instant createdAt,

                                   @JsonFormat(shape = JsonFormat.Shape.STRING)
                                   Instant updateAt) {

}
