package com.example.shoppinglist.service.impl;

import com.example.shoppinglist.entity.Item;
import com.example.shoppinglist.exception.ResourceNotFoundException;
import com.example.shoppinglist.mapper.ItemMapper;
import com.example.shoppinglist.model.ItemResponse;
import com.example.shoppinglist.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    private ItemServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ItemServiceImpl(itemRepository);
    }

    @Test
    void findAll_mapsEntitiesToResponses() {
        Item i1 = mock(Item.class);
        Item i2 = mock(Item.class);
        when(itemRepository.findAll()).thenReturn(List.of(i1, i2));

        try (MockedStatic<ItemMapper> mocked = mockStatic(ItemMapper.class)) {
            ItemResponse r1 = mock(ItemResponse.class);
            ItemResponse r2 = mock(ItemResponse.class);
            mocked.when(() -> ItemMapper.toResponse(i1)).thenReturn(r1);
            mocked.when(() -> ItemMapper.toResponse(i2)).thenReturn(r2);

            List<ItemResponse> out = service.findAll();

            assertEquals(2, out.size());
            assertSame(r1, out.get(0));
            assertSame(r2, out.get(1));
            verify(itemRepository).findAll();
        }
    }

    @Test
    void findById_whenFound_returnsMappedResponse() {
        Item entity = mock(Item.class);
        when(itemRepository.findById(42L)).thenReturn(Optional.of(entity));

        try (MockedStatic<ItemMapper> mocked = mockStatic(ItemMapper.class)) {
            ItemResponse resp = mock(ItemResponse.class);
            mocked.when(() -> ItemMapper.toResponse(entity)).thenReturn(resp);

            ItemResponse out = service.findById(42L);
            assertSame(resp, out);
            verify(itemRepository).findById(42L);
        }
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
        verify(itemRepository).findById(99L);
    }
}
