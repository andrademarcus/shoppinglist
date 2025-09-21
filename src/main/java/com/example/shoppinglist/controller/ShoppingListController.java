package com.example.shoppinglist.controller;

import com.example.shoppinglist.model.ShoppingListItemRequest;
import com.example.shoppinglist.model.ShoppingListRequest;
import com.example.shoppinglist.model.ShoppingListResponse;
import com.example.shoppinglist.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @GetMapping
    @Operation(
            summary = "List shopping lists",
            description = "Returns all shopping lists with their items."
    )
    public ResponseEntity<List<ShoppingListResponse>> getAllShoppingLists() {
        return ResponseEntity.ok(shoppingListService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get shopping list",
            description = "Returns a shopping list by ID, including its items."
    )
    public ResponseEntity<ShoppingListResponse> getShoppingListById(@PathVariable Long id) {
        return ResponseEntity.ok(shoppingListService.findById(id));
    }

    @PostMapping("/{id}/items")
    @Operation(
            summary = "Add item to list",
            description = "Adds an existing catalog item to the shopping list. Body: { itemId, quantity }."
    )
    public ResponseEntity<ShoppingListResponse> addItem(@PathVariable Long id, @Valid @RequestBody ShoppingListItemRequest itemRequest) {
        return ResponseEntity.ok(shoppingListService.addItem(id, itemRequest));
    }

    @DeleteMapping("/{listId}/items/{listItemId}")
    @Operation(
            summary = "Remove item from list",
            description = "Deletes a specific line item from the shopping list. Returns 204 No Content."
    )
    public ResponseEntity<Void> deleteItem(@PathVariable Long listId,
                                           @PathVariable Long listItemId) {
        shoppingListService.deleteItem(listId, listItemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(
            summary = "Create shopping list",
            description = "Creates a new shopping list. Returns 201 Created with Location header."
    )
    public ResponseEntity<ShoppingListResponse> createShoppingList(@Valid @RequestBody ShoppingListRequest listRequest) {
        ShoppingListResponse created = shoppingListService.save(listRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete shopping list",
            description = "Deletes a shopping list by ID. Returns 204 No Content."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shoppingListService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update shopping list",
            description = "Partially updates list fields (e.g., name)."
    )
    public ResponseEntity<ShoppingListResponse> updateShoppingList(@PathVariable Long id,
                                                                   @Valid @RequestBody ShoppingListRequest listRequest) {
        return ResponseEntity.ok(shoppingListService.update(id, listRequest));
    }

    @PatchMapping("/{listId}/items/{listItemId}")
    @Operation(
            summary = "Update item quantity",
            description = "Updates quantity of a line item in the shopping list. Body: { quantity }."
    )
    public ResponseEntity<ShoppingListResponse> updateItem(@PathVariable Long listId,
                                           @PathVariable Long listItemId,
                                           @Valid @RequestBody ShoppingListItemRequest itemRequest) {
        return ResponseEntity.ok(shoppingListService.updateItemQuantity(listId, listItemId, itemRequest.quantity()));
    }

}