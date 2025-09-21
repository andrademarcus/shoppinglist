package com.example.shoppinglist.bootstrap;

import com.example.shoppinglist.entity.Category;
import com.example.shoppinglist.entity.Item;
import com.example.shoppinglist.entity.ShoppingList;
import com.example.shoppinglist.entity.ShoppingListItem;
import com.example.shoppinglist.repository.CategoryRepository;
import com.example.shoppinglist.repository.ItemRepository;
import com.example.shoppinglist.repository.ShoppingListItemRepository;
import com.example.shoppinglist.repository.ShoppingListRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final ObjectMapper objectMapper;

    public DataLoader(CategoryRepository categoryRepository,
                      ItemRepository itemRepository,
                      ShoppingListRepository shoppingListRepository,
                      ShoppingListItemRepository shoppingListItemRepository,
                      ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (categoryRepository.count() > 0 || itemRepository.count() > 0) {
            return;
        }

        try (InputStream inputStream = new ClassPathResource("data/initial.json").getInputStream()) {

            logger.info("Loading data...");

            Seed rootSeed = objectMapper.readValue(inputStream, Seed.class);

            // categories
            if (rootSeed.categories != null) {
                for (CategorySeed category : rootSeed.categories) {
                    categoryRepository.findByNameIgnoreCase(category.name)
                            .orElseGet(() -> categoryRepository.save(new Category(category.name)));
                }
            }

            // items
            if (rootSeed.items != null) {
                for (ItemSeed item : rootSeed.items) {

                    var category = categoryRepository.findByNameIgnoreCase(item.categoryName);
                    if (category.isEmpty()) {
                        logger.warn("Unable to add item {}. Category {} not found", item.name, item.categoryName);
                        continue;
                    }

                    itemRepository.findByNameAndCategory(item.name, item.categoryName)
                            .orElseGet(() -> {
                                Item e = new Item();
                                e.setName(item.name);
                                e.setPrice(item.price);
                                e.setCategory(category.get());
                                return itemRepository.save(e);
                            });
                }
            }

            // items
            if (rootSeed.shoppingList != null) {
                for (ShoppingListSeed seed : rootSeed.shoppingList) {

                    var item = itemRepository.findByNameAndCategory(seed.itemName, seed.categoryName);
                    if (item.isEmpty()) {
                        logger.warn("Unable to add an unknown item {} - {} to shopping list {}", seed.itemName, seed.categoryName, seed.name);
                        continue;
                    }

                    // shopping list
                    shoppingListRepository.findByNameIgnoreCase(seed.name)
                            .orElseGet(() -> shoppingListRepository.save(new ShoppingList(seed.name)));

                    // shopping list item
                    shoppingListRepository.findByNameIgnoreCase(seed.name)
                            .map(i -> {
                                ShoppingListItem shoppingListItem = new ShoppingListItem();
                                shoppingListItem.setQuantity(seed.quantity);
                                shoppingListItem.setItem(item.get());
                                shoppingListItem.setShoppingList(i);
                                return shoppingListItemRepository.save(shoppingListItem);
                            });

                }
            }

            logger.info("Finished loading data...");

        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Seed {
        public List<CategorySeed> categories;
        public List<ItemSeed> items;
        public List<ShoppingListSeed> shoppingList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CategorySeed {
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ItemSeed {
        public String name;

        @JsonProperty("category")
        public String categoryName;
        public BigDecimal price;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ShoppingListSeed {
        public String name;

        @JsonProperty("category")
        public String categoryName;

        @JsonProperty("item")
        public String itemName;
        public Integer quantity;

    }

}