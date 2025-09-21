package com.example.shoppinglist.model;

import com.example.shoppinglist.entity.ShoppingListItem;

import java.util.List;

public record ShoppingListResponse(Long id, String name, List<ShoppingListItem> items) {

}
