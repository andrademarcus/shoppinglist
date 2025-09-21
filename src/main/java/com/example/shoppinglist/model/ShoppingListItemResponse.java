package com.example.shoppinglist.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;

public record ShoppingListItemResponse(ItemResponse item,
                                       Integer quantity,

                                       @JsonFormat(shape = JsonFormat.Shape.STRING)
                                       Instant createdAt,

                                       @JsonFormat(shape = JsonFormat.Shape.STRING)
                                       Instant updatedAt) {

    public BigDecimal getCost() {
        return item.price().multiply(BigDecimal.valueOf(quantity));
    }

}
