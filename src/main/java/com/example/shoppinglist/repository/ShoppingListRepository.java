package com.example.shoppinglist.repository;

import com.example.shoppinglist.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findByNameIgnoreCase(String name);
}