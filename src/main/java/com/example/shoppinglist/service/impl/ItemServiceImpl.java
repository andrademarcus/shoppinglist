package com.example.shoppinglist.service.impl;

import com.example.shoppinglist.exception.ResourceNotFoundException;
import com.example.shoppinglist.mapper.ItemMapper;
import com.example.shoppinglist.model.ItemResponse;
import com.example.shoppinglist.repository.ItemRepository;
import com.example.shoppinglist.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ItemResponse> findAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toResponse)
                .toList();
    }

    @Override
    public ItemResponse findById(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Item %d not found".formatted(id)));
    }
}
