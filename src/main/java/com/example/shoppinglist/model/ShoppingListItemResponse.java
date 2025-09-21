package com.example.shoppinglist.model;

import java.math.BigDecimal;

public record ShoppingListItemResponse(Long id, String name, BigDecimal price, Integer quantity, CategoryResponse category) {

    public BigDecimal getCost() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

}
