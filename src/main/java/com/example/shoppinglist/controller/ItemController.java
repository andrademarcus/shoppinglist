package com.example.shoppinglist.controller;

import com.example.shoppinglist.model.ItemResponse;
import com.example.shoppinglist.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    @Operation(
            summary = "List items",
            description = "Returns all items."
    )
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        return ResponseEntity.ok(itemService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get item",
            description = "Returns a item by ID, including its category."
    )
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

}
