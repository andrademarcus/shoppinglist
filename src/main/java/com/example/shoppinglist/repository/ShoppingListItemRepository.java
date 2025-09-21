package com.example.shoppinglist.repository;

import com.example.shoppinglist.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {


}
