package com.example.inventory.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Item {
    
    @JsonProperty
    private long id;
    
    @JsonProperty
    @NotNull
    private String name;
    
    @JsonProperty
    private String description;
    
    @JsonProperty
    @NotNull
    private String sku;
    
    @JsonProperty
    @Min(0)
    private int quantity;
    
    @JsonProperty
    @Min(0)
    private BigDecimal price;
    
    @JsonProperty
    private String category;
    
    @JsonProperty
    private LocalDateTime createdAt;
    
    @JsonProperty
    private LocalDateTime updatedAt;

    public Item() {
    }

    public Item(long id, String name, String description, String sku, int quantity, 
                BigDecimal price, String category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return id == item.id && 
               quantity == item.quantity && 
               Objects.equals(name, item.name) && 
               Objects.equals(sku, item.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sku, quantity);
    }
}