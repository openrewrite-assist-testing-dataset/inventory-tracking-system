package com.example.inventory.db;

import com.example.inventory.core.Item;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Item.class)
public interface ItemDAO {

    @SqlQuery("SELECT * FROM items WHERE id = :id")
    Optional<Item> findById(@Bind("id") long id);

    @SqlQuery("SELECT * FROM items WHERE sku = :sku")
    Optional<Item> findBySku(@Bind("sku") String sku);

    @SqlQuery("SELECT * FROM items WHERE category = :category")
    List<Item> findByCategory(@Bind("category") String category);

    @SqlQuery("SELECT * FROM items ORDER BY name")
    List<Item> findAll();

    @SqlQuery("SELECT * FROM items WHERE quantity < :threshold")
    List<Item> findLowStock(@Bind("threshold") int threshold);

    @SqlUpdate("INSERT INTO items (name, description, sku, quantity, price, category, created_at, updated_at) " +
               "VALUES (:name, :description, :sku, :quantity, :price, :category, NOW(), NOW())")
    @GetGeneratedKeys
    long insert(@BindBean Item item);

    @SqlUpdate("UPDATE items SET name = :name, description = :description, quantity = :quantity, " +
               "price = :price, category = :category, updated_at = NOW() WHERE id = :id")
    void update(@BindBean Item item);

    @SqlUpdate("UPDATE items SET quantity = quantity + :adjustment WHERE id = :id")
    void adjustQuantity(@Bind("id") long id, @Bind("adjustment") int adjustment);

    @SqlUpdate("DELETE FROM items WHERE id = :id")
    void deleteById(@Bind("id") long id);
}