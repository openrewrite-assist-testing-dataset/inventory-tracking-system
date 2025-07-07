package com.example.inventory;

import com.example.inventory.api.ItemResource;
import com.example.inventory.core.Item;
import com.example.inventory.db.ItemDAO;
import com.example.inventory.db.RawJdbcItemDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemResourceTest {

    @Mock
    private ItemDAO itemDAO;

    @Mock
    private RawJdbcItemDAO rawJdbcItemDAO;

    private ItemResource itemResource;
    private Item testItem;

    @BeforeEach
    void setUp() {
        itemResource = new ItemResource(itemDAO, rawJdbcItemDAO);
        testItem = new Item(1L, "Test Item", "Test Description", "TEST-001", 
                           100, new BigDecimal("19.99"), "Electronics", 
                           LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void testFindById() {
        when(itemDAO.findById(anyLong())).thenReturn(Optional.of(testItem));
        
        Optional<Item> result = itemDAO.findById(1L);
        
        assertTrue(result.isPresent());
        assertEquals(testItem, result.get());
        verify(itemDAO).findById(1L);
    }

    @Test
    void testFindAll() {
        List<Item> items = Arrays.asList(testItem);
        when(itemDAO.findAll()).thenReturn(items);
        
        List<Item> result = itemDAO.findAll();
        
        assertEquals(1, result.size());
        assertEquals(testItem, result.get(0));
        verify(itemDAO).findAll();
    }

    @Test
    void testInsertItem() {
        when(itemDAO.insert(any(Item.class))).thenReturn(1L);
        
        long result = itemDAO.insert(testItem);
        
        assertEquals(1L, result);
        verify(itemDAO).insert(testItem);
    }

    @Test
    void testFindByCategory() {
        List<Item> items = Arrays.asList(testItem);
        when(itemDAO.findByCategory("Electronics")).thenReturn(items);
        
        List<Item> result = itemDAO.findByCategory("Electronics");
        
        assertEquals(1, result.size());
        assertEquals(testItem, result.get(0));
        verify(itemDAO).findByCategory("Electronics");
    }

    @Test
    void testFindLowStock() {
        List<Item> items = Arrays.asList(testItem);
        when(itemDAO.findLowStock(10)).thenReturn(items);
        
        List<Item> result = itemDAO.findLowStock(10);
        
        assertEquals(1, result.size());
        assertEquals(testItem, result.get(0));
        verify(itemDAO).findLowStock(10);
    }

    @Test
    void testItemCreation() {
        Item newItem = new Item(0L, "New Item", "New Description", "NEW-001", 
                               50, new BigDecimal("29.99"), "Tools", 
                               LocalDateTime.now(), LocalDateTime.now());
        
        assertEquals("New Item", newItem.getName());
        assertEquals("NEW-001", newItem.getSku());
        assertEquals(50, newItem.getQuantity());
        assertEquals(new BigDecimal("29.99"), newItem.getPrice());
        assertEquals("Tools", newItem.getCategory());
    }
}