package com.example.shoppinglist.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ShoppingListRequest(@NotEmpty
                                  String name,
                                  List<ShoppingListItemRequest> items) {

}
