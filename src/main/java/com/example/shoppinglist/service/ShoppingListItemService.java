package com.example.shoppinglist.service;

import com.example.shoppinglist.model.ShoppingListItemResponse;

import java.util.List;

public interface ShoppingListItemService {

    List<ShoppingListItemResponse> findAll();
    List<ShoppingListItemResponse> findAllByShoppingListId(Long shoppingListId);
    List<ShoppingListItemResponse> findAllByShoppingListItemId(Long shoppingListItemId);
    ShoppingListItemResponse findById(Long id);
    ShoppingListItemResponse save(ShoppingListItemResponse shoppingListItemResponse);
    ShoppingListItemResponse update(ShoppingListItemResponse shoppingListItemResponse);
    void delete(Long id);

}
