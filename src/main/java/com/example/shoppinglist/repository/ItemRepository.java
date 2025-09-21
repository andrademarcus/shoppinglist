package com.example.shoppinglist.repository;

import com.example.shoppinglist.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
           SELECT i FROM Item i
           WHERE LOWER(i.name) = LOWER(:name)
             AND LOWER(i.category.name) = LOWER(:category)
           """)
    Optional<Item> findByNameAndCategory(@Param("name") String name,
                                         @Param("category") String categoryName);

}