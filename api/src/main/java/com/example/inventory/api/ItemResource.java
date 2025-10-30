package com.example.inventory.api;

import com.example.inventory.core.Item;
import com.example.inventory.core.User;
import com.example.inventory.db.ItemDAO;
import com.example.inventory.db.RawJdbcItemDAO;
import io.dropwizard.auth.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Path("/api/v1/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Items", description = "Operations for managing inventory items")
public class ItemResource {

    private final ItemDAO itemDAO;
    private final RawJdbcItemDAO rawJdbcItemDAO;

    public ItemResource(ItemDAO itemDAO, RawJdbcItemDAO rawJdbcItemDAO) {
        this.itemDAO = itemDAO;
        this.rawJdbcItemDAO = rawJdbcItemDAO;
    }

    @GET
    @Operation(summary = "Get all items", description = "Returns a list of all items in inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved items"),
        @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
        @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden")
    })
    public List<Item> getAllItems(@Auth User user) {
        return itemDAO.findAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get item by ID", description = "Returns a single item")
    public Response getItem(@PathParam("id") long id, @Auth User user) {
        Optional<Item> item = itemDAO.findById(id);
        if (item.isPresent()) {
            return Response.ok(item.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/category/{category}")
    @Operation(summary = "Get items by category", description = "Returns items in a specific category")
    public List<Item> getItemsByCategory(@PathParam("category") String category, @Auth User user) {
        return itemDAO.findByCategory(category);
    }

    @GET
    @Path("/low-stock")
    @Operation(summary = "Get low stock items", description = "Returns items with quantity below threshold")
    public List<Item> getLowStockItems(@QueryParam("threshold") @DefaultValue("10") int threshold, @Auth User user) {
        return itemDAO.findLowStock(threshold);
    }

    @GET
    @Path("/expensive")
    @Operation(summary = "Get expensive items", description = "Returns items above a price threshold")
    public Response getExpensiveItems(@QueryParam("minPrice") @DefaultValue("100.0") double minPrice, @Auth User user) {
        try {
            List<Item> items = rawJdbcItemDAO.findExpensiveItems(minPrice);
            return Response.ok(items).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Operation(summary = "Create a new item", description = "Creates a new inventory item")
    public Response createItem(@Valid Item item, @Auth User user) {
        if (item.getName() == null || item.getSku() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        long id = itemDAO.insert(item);
        item.setId(id);
        
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an item", description = "Updates an existing inventory item")
    public Response updateItem(@PathParam("id") long id, @Valid Item item, @Auth User user) {
        Optional<Item> existingItem = itemDAO.findById(id);
        if (!existingItem.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        item.setId(id);
        itemDAO.update(item);
        return Response.ok(item).build();
    }

    @PUT
    @Path("/{id}/quantity")
    @Operation(summary = "Adjust item quantity", description = "Adjusts the quantity of an item")
    public Response adjustQuantity(@PathParam("id") long id, 
                                   @QueryParam("adjustment") int adjustment, 
                                   @Auth User user) {
        Optional<Item> existingItem = itemDAO.findById(id);
        if (!existingItem.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        itemDAO.adjustQuantity(id, adjustment);
        return Response.ok().build();
    }
}