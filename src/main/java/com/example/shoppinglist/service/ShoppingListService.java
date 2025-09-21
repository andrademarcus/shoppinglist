package com.example.shoppinglist.service;

import com.example.shoppinglist.model.ShoppingListItemRequest;
import com.example.shoppinglist.model.ShoppingListRequest;
import com.example.shoppinglist.model.ShoppingListResponse;

import java.util.List;

public interface ShoppingListService {

    List<ShoppingListResponse> findAll();
    ShoppingListResponse findById(Long id);
    ShoppingListResponse addItem(Long id, ShoppingListItemRequest itemRequest);
    void deleteItem(Long listId, Long listItemId);
    ShoppingListResponse save(ShoppingListRequest shoppingListRequest);
    ShoppingListResponse update(Long id, ShoppingListRequest shoppingListRequest);
    ShoppingListResponse updateItemQuantity(Long listId, Long listItemId, Integer quantity);
    void delete(Long id);

}
