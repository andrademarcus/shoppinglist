package com.example.shoppinglist.controller;

import com.example.shoppinglist.exception.GlobalExceptionHandler;
import com.example.shoppinglist.exception.ResourceNotFoundException;
import com.example.shoppinglist.model.CategoryResponse;
import com.example.shoppinglist.model.ItemResponse;
import com.example.shoppinglist.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
@Import(GlobalExceptionHandler.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getAllItems_ok() throws Exception {

        CategoryResponse category = new CategoryResponse(1L, "Clothes");

        ItemResponse i1 = new ItemResponse(1L, category, "T-Shirt Blue", new BigDecimal("10.23"));
        ItemResponse i2 = new ItemResponse(2L, category, "T-Shirt White", new BigDecimal("20.50"));
        when(itemService.findAll()).thenReturn(List.of(i1, i2));

        mockMvc.perform(get("/api/items/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("T-Shirt Blue"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("T-Shirt White"));
    }

    @Test
    void getItemById_ok() throws Exception {

        CategoryResponse category = new CategoryResponse(1L, "Shoes");

        ItemResponse dto = new ItemResponse(10L, category, "Nike Shox", new BigDecimal("5.00"));
        when(itemService.findById(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/items/{id}", 10)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Nike Shox"));
    }

    @Test
    void getItemById_notFound() throws Exception {
        when(itemService.findById(anyLong())).thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(get("/api/items/{id}", 999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
