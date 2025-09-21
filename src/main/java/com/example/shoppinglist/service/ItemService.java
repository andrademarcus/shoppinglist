package com.example.shoppinglist.service;

import com.example.shoppinglist.model.ItemResponse;

import java.util.List;

public interface ItemService {
    List<ItemResponse> findAll();
    ItemResponse findById(Long id);
}
