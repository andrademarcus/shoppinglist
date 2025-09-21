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
import com.example.shoppinglist.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private final Logger logger = LoggerFactory.getLogger(ShoppingListServiceImpl.class);

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final ItemRepository itemRepository;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository, ShoppingListItemRepository shoppingListItemRepository, ItemRepository itemRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ShoppingListResponse> findAll() {
        return shoppingListRepository.findAll()
                .stream()
                .map(ShoppingListMapper::toResponse).toList();
    }

    @Override
    public ShoppingListResponse findById(Long id) {
        return shoppingListRepository.findById(id)
                .map(ShoppingListMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping List %d not found".formatted(id)));
    }

    @Override
    public ShoppingListResponse addItem(Long id, ShoppingListItemRequest itemRequest) {

        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping List %d not found".formatted(id)));

        Item item = itemRepository.findById(itemRequest.itemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item %d not found".formatted(itemRequest.itemId())));

        Optional<ShoppingListItem> optionalShoppingListItem = shoppingListItemRepository
                .findByShoppingListAndItemId(shoppingList.getId(), item.getId());

        // increment quantity if item already exists
        if (optionalShoppingListItem.isPresent()) {
            optionalShoppingListItem.get().setQuantity(optionalShoppingListItem.get().getQuantity() + itemRequest.quantity());
            shoppingListRepository.save(optionalShoppingListItem.get().getShoppingList());
        } else {

            // create a new record
            ShoppingListItem shoppingListItem = new ShoppingListItem();
            shoppingListItem.setQuantity(itemRequest.quantity());
            shoppingListItem.setItem(item);
            shoppingListItem.setShoppingList(shoppingList);
            shoppingList.getItems().add(shoppingListItem);
            shoppingListRepository.save(shoppingList);
        }

        return ShoppingListMapper.toResponse(shoppingList);
    }

    @Override
    public void deleteItem(Long id, Long listItemId) {

        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping List %d not found".formatted(id)));

        shoppingListItemRepository
                .findByShoppingListAndItemId(shoppingList.getId(), listItemId)
                .ifPresent(shoppingListItemRepository::delete);

    }

    @Override
    public ShoppingListResponse save(ShoppingListRequest shoppingListRequest) {

        if (shoppingListRepository.findByNameIgnoreCase(shoppingListRequest.name()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Shopping List %s already exists".formatted(shoppingListRequest.name())
            );
        }

        final ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(shoppingListRequest.name());

        if (shoppingListRequest.items() != null) {
            shoppingListRequest.items().forEach(i -> {

                Optional<Item> optionalItem = itemRepository.findById(i.itemId());
                if (optionalItem.isPresent()) {
                    ShoppingListItem shoppingListItem = new ShoppingListItem();
                    shoppingListItem.setQuantity(i.quantity());
                    shoppingListItem.setItem(optionalItem.get());
                    shoppingListItem.setShoppingList(shoppingList);
                    shoppingList.getItems().add(shoppingListItem);
                } else {
                    logger.warn("Item not found {}. Ignoring insertion.", i.itemId());
                }

            });
        }

        shoppingListRepository.save(shoppingList);
        return ShoppingListMapper.toResponse(shoppingList);

    }

    @Override
    public ShoppingListResponse update(Long id, ShoppingListRequest shoppingListRequest) {

        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping List %d not found".formatted(id)));

        shoppingList.setName(shoppingListRequest.name());

        return ShoppingListMapper.toResponse(shoppingList);
    }

    @Override
    public ShoppingListResponse updateItemQuantity(Long listId, Long listItemId, Integer quantity) {

        ShoppingListItem shoppingListItem = shoppingListItemRepository
                .findByShoppingListAndItemId(listId, listItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping List Item %d not found at %d list".formatted(listItemId, listId)));

        shoppingListItem.setQuantity(quantity);
        shoppingListItemRepository.save(shoppingListItem);

        return ShoppingListMapper.toResponse(shoppingListItem.getShoppingList());

    }

    @Override
    public void delete(Long id) {
         shoppingListRepository.findById(id)
                .ifPresent(i -> shoppingListRepository.delete(i));
    }
}
