package com.example.shoppinglist.service.impl;

import com.example.shoppinglist.entity.Item;
import com.example.shoppinglist.entity.ShoppingList;
import com.example.shoppinglist.entity.ShoppingListItem;
import com.example.shoppinglist.exception.ResourceAlreadyExistsException;
import com.example.shoppinglist.exception.ResourceNotFoundException;
import com.example.shoppinglist.mapper.ShoppingListMapper;
import com.example.shoppinglist.model.ShoppingListItemRequest;
import com.example.shoppinglist.model.ShoppingListRequest;
import com.example.shoppinglist.model.ShoppingListResponse;
import com.example.shoppinglist.repository.ItemRepository;
import com.example.shoppinglist.repository.ShoppingListItemRepository;
import com.example.shoppinglist.repository.ShoppingListRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShoppingListServiceImplTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ShoppingListItemRepository shoppingListItemRepository;

    @Mock
    private ItemRepository itemRepository;

    private ShoppingListServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ShoppingListServiceImpl(shoppingListRepository, shoppingListItemRepository, itemRepository);
    }

    @Test
    void findAll_returnsMappedResponses() {
        ShoppingList sl1 = new ShoppingList("List A");
        ShoppingList sl2 = new ShoppingList("List B");
        when(shoppingListRepository.findAll()).thenReturn(List.of(sl1, sl2));

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            mocked.when(() -> ShoppingListMapper.toResponse(any())).thenReturn(mock(ShoppingListResponse.class));

            List<ShoppingListResponse> out = service.findAll();
            assertEquals(2, out.size());
            verify(shoppingListRepository).findAll();
        }
    }

    @Test
    void findById_whenFound_returnsMapped() {
        ShoppingList sl = new ShoppingList("List A");
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(sl));

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            ShoppingListResponse resp = mock(ShoppingListResponse.class);
            mocked.when(() -> ShoppingListMapper.toResponse(sl)).thenReturn(resp);

            ShoppingListResponse out = service.findById(1L);
            assertSame(resp, out);
        }
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        when(shoppingListRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void addItem_whenLineExists_incrementsQuantityAndSavesList() {
        ShoppingList list = new ShoppingList("List");
        list.setId(1L);
        Item item = new Item();
        item.setId(10L);

        ShoppingListItem line = new ShoppingListItem();
        line.setShoppingList(list);
        line.setItem(item);
        line.setQuantity(2);

        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(shoppingListItemRepository.findByShoppingListAndItemId(1L, 10L))
                .thenReturn(Optional.of(line));

        ShoppingListItemRequest req = new ShoppingListItemRequest(10L, 3);

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            mocked.when(() -> ShoppingListMapper.toResponse(list)).thenReturn(mock(ShoppingListResponse.class));

            service.addItem(1L, req);

            assertEquals(5, line.getQuantity());
            verify(shoppingListRepository).save(list);
        }
    }

    @Test
    void addItem_whenNew_createsLineAndSavesList() {
        ShoppingList list = new ShoppingList("List");
        list.setId(1L);
        Item item = new Item();
        item.setId(10L);

        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(shoppingListItemRepository.findByShoppingListAndItemId(1L, 10L))
                .thenReturn(Optional.empty());

        ShoppingListItemRequest req = new ShoppingListItemRequest(10L, 2);

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            mocked.when(() -> ShoppingListMapper.toResponse(list)).thenReturn(mock(ShoppingListResponse.class));

            service.addItem(1L, req);

            assertEquals(1, list.getItems().size());
            ShoppingListItem created = list.getItems().get(0);
            assertEquals(2, created.getQuantity());
            assertSame(item, created.getItem());
            assertSame(list, created.getShoppingList());
            verify(shoppingListRepository).save(list);
        }
    }

    @Test
    void addItem_whenListMissing_throwsNotFound() {
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.empty());
        ShoppingListItemRequest req = new ShoppingListItemRequest(10L, 1);
        assertThrows(ResourceNotFoundException.class, () -> service.addItem(1L, req));
    }

    @Test
    void addItem_whenItemMissing_throwsNotFound() {
        ShoppingList list = new ShoppingList("MyList");
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        ShoppingListItemRequest req = new ShoppingListItemRequest(10L, 1);
        assertThrows(ResourceNotFoundException.class, () -> service.addItem(1L, req));
    }

    @Test
    void deleteItem_whenPresent_deletes() {
        ShoppingList list = new ShoppingList("MyList");
        list.setId(1L);

        ShoppingListItem line = new ShoppingListItem();
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.of(list));
        when(shoppingListItemRepository.findByShoppingListAndItemId(1L, 50L))
                .thenReturn(Optional.of(line));

        service.deleteItem(1L, 50L);

        verify(shoppingListItemRepository).delete(line);
    }

    @Test
    void deleteItem_whenListMissing_throwsNotFound() {
        when(shoppingListRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteItem(1L, 1L));
    }

    @Test
    void save_whenNameExists_throwsConflict() {
        when(shoppingListRepository.findByNameIgnoreCase("Groceries")).thenReturn(Optional.of(new ShoppingList()));
        ShoppingListRequest req = new ShoppingListRequest("Groceries", List.of());

        assertThrows(ResourceAlreadyExistsException.class, () -> service.save(req));
    }

    @Test
    void save_createsListAndMapsResponse() {
        ShoppingListRequest req = new ShoppingListRequest("Groceries", List.of());
        when(shoppingListRepository.findByNameIgnoreCase("Groceries")).thenReturn(Optional.empty());

        // save(list) returns the same instance typically
        when(shoppingListRepository.save(any(ShoppingList.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            ShoppingListResponse resp = mock(ShoppingListResponse.class);
            mocked.when(() -> ShoppingListMapper.toResponse(any(ShoppingList.class))).thenReturn(resp);

            ShoppingListResponse out = service.save(req);
            assertSame(resp, out);
            verify(shoppingListRepository).save(any(ShoppingList.class));
        }
    }

    @Test
    void update_whenFound_setsName_andMaps() {
        ShoppingList list = new ShoppingList("Old");
        when(shoppingListRepository.findById(5L)).thenReturn(Optional.of(list));

        ShoppingListRequest req = new ShoppingListRequest("New", null);

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            ShoppingListResponse resp = mock(ShoppingListResponse.class);
            mocked.when(() -> ShoppingListMapper.toResponse(list)).thenReturn(resp);

            ShoppingListResponse out = service.update(5L, req);
            assertEquals("New", list.getName());
            assertSame(resp, out);
        }
    }

    @Test
    void update_whenMissing_throwsNotFound() {
        when(shoppingListRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(5L, new ShoppingListRequest("Name", null)));
    }

    @Test
    void updateItemQuantity_updatesAndSaves() {
        ShoppingList list = new ShoppingList("L");
        list.setId(1L);

        ShoppingListItem li = new ShoppingListItem();
        li.setShoppingList(list);
        li.setQuantity(2);

        when(shoppingListItemRepository.findByShoppingListAndItemId(1L, 9L))
                .thenReturn(Optional.of(li));

        try (MockedStatic<ShoppingListMapper> mocked = mockStatic(ShoppingListMapper.class)) {
            ShoppingListResponse resp = mock(ShoppingListResponse.class);
            mocked.when(() -> ShoppingListMapper.toResponse(list)).thenReturn(resp);

            ShoppingListResponse out = service.updateItemQuantity(1L, 9L, 7);

            assertEquals(7, li.getQuantity());
            verify(shoppingListItemRepository).save(li);
            assertSame(resp, out);
        }
    }

    @Test
    void updateItemQuantity_whenMissing_throwsNotFound() {
        when(shoppingListItemRepository.findByShoppingListAndItemId(1L, 9L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateItemQuantity(1L, 9L, 7));
    }

    @Test
    void delete_whenPresent_deletes() {
        ShoppingList list = new ShoppingList("L");
        when(shoppingListRepository.findById(3L)).thenReturn(Optional.of(list));

        service.delete(3L);

        verify(shoppingListRepository).delete(list);
    }

    @Test
    void delete_whenMissing_noop() {
        when(shoppingListRepository.findById(3L)).thenReturn(Optional.empty());

        service.delete(3L);

        verify(shoppingListRepository, never()).delete(any());
    }
}