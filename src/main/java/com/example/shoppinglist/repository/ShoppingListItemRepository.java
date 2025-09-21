package com.example.shoppinglist.repository;

import com.example.shoppinglist.entity.ShoppingList;
import com.example.shoppinglist.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    @Query("SELECT e FROM ShoppingListItem e WHERE e.shoppingList.id = :shoppingListId and e.item.id = :itemId")
    Optional<ShoppingListItem> findByShoppingListAndItemId(Long shoppingListId, Long itemId);

}
