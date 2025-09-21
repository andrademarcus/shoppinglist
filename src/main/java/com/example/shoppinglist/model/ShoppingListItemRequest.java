package com.example.shoppinglist.model;

import jakarta.validation.constraints.Min;

public record ShoppingListItemRequest(Long itemId,
                                      @Min(1) Integer quantity) {

}
