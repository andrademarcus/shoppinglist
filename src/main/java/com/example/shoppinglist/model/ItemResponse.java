package com.example.shoppinglist.model;

import java.math.BigDecimal;

public record ItemResponse(Long id,
                           CategoryResponse category,
                           String name,
                           BigDecimal price) {

}
