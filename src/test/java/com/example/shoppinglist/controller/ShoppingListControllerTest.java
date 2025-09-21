package com.example.shoppinglist.controller;

import com.example.shoppinglist.exception.GlobalExceptionHandler;
import com.example.shoppinglist.exception.ResourceNotFoundException;
import com.example.shoppinglist.model.ShoppingListItemRequest;
import com.example.shoppinglist.model.ShoppingListRequest;
import com.example.shoppinglist.model.ShoppingListResponse;
import com.example.shoppinglist.service.ShoppingListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShoppingListController.class)
@Import(GlobalExceptionHandler.class)
public class ShoppingListControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ShoppingListService shoppingListService;

    @Test
    @DisplayName("GET /api/shopping-lists -> 200 returns all lists")
    void getAllShoppingLists_ok() throws Exception {
        var r1 = new ShoppingListResponse(1L, "Groceries", List.of(), Instant.now(), Instant.now());
        var r2 = new ShoppingListResponse(2L, "Pharmacy", List.of(), Instant.now(), Instant.now());
        when(shoppingListService.findAll()).thenReturn(List.of(r1, r2));

        mvc.perform(get("/api/shopping-lists").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Groceries"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("GET /api/shopping-lists/{id} -> 200 when found")
    void getShoppingListById_ok() throws Exception {
        var resp = new ShoppingListResponse(10L, "Weekend", List.of(), Instant.now(), Instant.now());
        when(shoppingListService.findById(10L)).thenReturn(resp);

        mvc.perform(get("/api/shopping-lists/{id}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Weekend"));
    }

    @Test
    @DisplayName("GET /api/shopping-lists/{id} -> 404 when missing")
    void getShoppingListById_notFound() throws Exception {
        when(shoppingListService.findById(404L)).thenThrow(new ResourceNotFoundException("not found"));

        mvc.perform(get("/api/shopping-lists/{id}", 404))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/shopping-lists -> 201 with Location and body")
    void createShoppingList_created() throws Exception {
        var created = new ShoppingListResponse(5L, "Party", List.of(), Instant.now(), Instant.now());
        when(shoppingListService.save(any(ShoppingListRequest.class))).thenReturn(created);

        mvc.perform(post("/api/shopping-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Party\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/shopping-lists/5")))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Party"));
    }

    @Test
    @DisplayName("DELETE /api/shopping-lists/{id} -> 204")
    void deleteShoppingList_noContent() throws Exception {
        mvc.perform(delete("/api/shopping-lists/{id}", 3))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/shopping-lists/{id} -> 200 updates and returns")
    void updateShoppingList_ok() throws Exception {
        var updated = new ShoppingListResponse(7L, "Updated", List.of(), Instant.now(), Instant.now());
        when(shoppingListService.update(eq(7L), any(ShoppingListRequest.class))).thenReturn(updated);

        mvc.perform(patch("/api/shopping-lists/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("POST /api/shopping-lists/{id}/items -> 200 returns updated list")
    void addItem_ok() throws Exception {
        var after = new ShoppingListResponse(1L, "Groceries", List.of(), Instant.now(), Instant.now());
        when(shoppingListService.addItem(eq(1L), any(ShoppingListItemRequest.class))).thenReturn(after);

        mvc.perform(post("/api/shopping-lists/{id}/items", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":10,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/shopping-lists/{listId}/items/{listItemId} -> 200")
    void updateItemQuantity_ok() throws Exception {
        var after = new ShoppingListResponse(1L, "Groceries", List.of(), Instant.now(), Instant.now());
        when(shoppingListService.updateItemQuantity(1L, 9L, 4)).thenReturn(after);

        mvc.perform(patch("/api/shopping-lists/{listId}/items/{listItemId}", 1, 9)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/shopping-lists/{listId}/items/{listItemId} -> 204")
    void deleteItem_noContent() throws Exception {
        mvc.perform(delete("/api/shopping-lists/{listId}/items/{listItemId}", 1, 33))
                .andExpect(status().isNoContent());
    }
}
