package com.example.inventory.api;

import com.example.inventory.core.Item;
import com.example.inventory.core.User;
import com.example.inventory.db.ItemDAO;
import com.example.inventory.db.RawJdbcItemDAO;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;

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
@Api(value = "Items", description = "Operations for managing inventory items")
public class ItemResource {

    private final ItemDAO itemDAO;
    private final RawJdbcItemDAO rawJdbcItemDAO;

    public ItemResource(ItemDAO itemDAO, RawJdbcItemDAO rawJdbcItemDAO) {
        this.itemDAO = itemDAO;
        this.rawJdbcItemDAO = rawJdbcItemDAO;
    }

    @GET
    @ApiOperation(value = "Get all items", notes = "Returns a list of all items in inventory")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved items"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    public List<Item> getAllItems(@Auth User user) {
        return itemDAO.findAll();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get item by ID", notes = "Returns a single item")
    public Response getItem(@PathParam("id") long id, @Auth User user) {
        Optional<Item> item = itemDAO.findById(id);
        if (item.isPresent()) {
            return Response.ok(item.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/category/{category}")
    @ApiOperation(value = "Get items by category", notes = "Returns items in a specific category")
    public List<Item> getItemsByCategory(@PathParam("category") String category, @Auth User user) {
        return itemDAO.findByCategory(category);
    }

    @GET
    @Path("/low-stock")
    @ApiOperation(value = "Get low stock items", notes = "Returns items with quantity below threshold")
    public List<Item> getLowStockItems(@QueryParam("threshold") @DefaultValue("10") int threshold, @Auth User user) {
        return itemDAO.findLowStock(threshold);
    }

    @GET
    @Path("/expensive")
    @ApiOperation(value = "Get expensive items", notes = "Returns items above a price threshold")
    public Response getExpensiveItems(@QueryParam("minPrice") @DefaultValue("100.0") double minPrice, @Auth User user) {
        try {
            List<Item> items = rawJdbcItemDAO.findExpensiveItems(minPrice);
            return Response.ok(items).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @ApiOperation(value = "Create a new item", notes = "Creates a new inventory item")
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
    @ApiOperation(value = "Update an item", notes = "Updates an existing inventory item")
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
    @ApiOperation(value = "Adjust item quantity", notes = "Adjusts the quantity of an item")
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