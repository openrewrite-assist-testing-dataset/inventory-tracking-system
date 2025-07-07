package com.example.inventory;

import com.example.inventory.api.ItemResource;
import com.example.inventory.api.JwtAuthenticator;
import com.example.inventory.core.User;
import com.example.inventory.db.ItemDAO;
import com.example.inventory.db.RawJdbcItemDAO;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class InventoryApplication extends Application<InventoryConfiguration> {

    public static void main(String[] args) throws Exception {
        new InventoryApplication().run(args);
    }

    @Override
    public String getName() {
        return "inventory-tracking";
    }

    @Override
    public void initialize(Bootstrap<InventoryConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<InventoryConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(InventoryConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(InventoryConfiguration configuration, Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final ItemDAO itemDAO = jdbi.onDemand(ItemDAO.class);
        
        final DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), "mysql");
        final RawJdbcItemDAO rawJdbcItemDAO = new RawJdbcItemDAO(dataSource);

        // Setup JWT authentication
        environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new JwtAuthenticator(configuration.getJwtSecret()))
                        .setPrefix("Bearer")
                        .buildAuthFilter()));
        
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        // Register resources
        environment.jersey().register(new ItemResource(itemDAO, rawJdbcItemDAO));

        // Setup Swagger
        configureSwagger(environment);
    }

    private void configureSwagger(Environment environment) {
        BeanConfig config = new BeanConfig();
        config.setTitle("Inventory Tracking API");
        config.setVersion("1.0.0");
        config.setResourcePackage("com.example.inventory.api");
        config.setScan(true);
        
        environment.jersey().register(new ApiListingResource());
        environment.jersey().register(new SwaggerSerializers());
    }
}