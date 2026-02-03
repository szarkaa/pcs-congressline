package hu.congressline.pcs.config.liquibase;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.liquibase.autoconfigure.DataSourceClosingSpringLiquibase;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;
import org.springframework.core.env.Environment;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

public final class LiquibaseUtil {

    private LiquibaseUtil() {

    }

    @SuppressWarnings("MissingJavadocMethod")
    public static SpringLiquibase createSpringLiquibase(
        DataSource liquibaseDatasource,
        LiquibaseProperties liquibaseProperties,
        DataSource dataSource,
        DataSourceProperties dataSourceProperties
    ) {
        SpringLiquibase liquibase;
        DataSource liquibaseDataSource = getDataSource(liquibaseDatasource, liquibaseProperties, dataSource);
        if (liquibaseDataSource != null) {
            liquibase = new SpringLiquibase();
            liquibase.setDataSource(liquibaseDataSource);
            return liquibase;
        }
        liquibase = new DataSourceClosingSpringLiquibase();
        liquibase.setDataSource(createNewDataSource(liquibaseProperties, dataSourceProperties));
        return liquibase;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static AsyncSpringLiquibase createAsyncSpringLiquibase(
        Environment env,
        Executor executor,
        DataSource liquibaseDatasource,
        LiquibaseProperties liquibaseProperties,
        DataSource dataSource,
        DataSourceProperties dataSourceProperties
    ) {
        AsyncSpringLiquibase liquibase = new AsyncSpringLiquibase(executor, env);
        DataSource liquibaseDataSource = getDataSource(liquibaseDatasource, liquibaseProperties, dataSource);
        if (liquibaseDataSource != null) {
            liquibase.setCloseDataSourceOnceMigrated(false);
            liquibase.setDataSource(liquibaseDataSource);
        } else {
            liquibase.setDataSource(createNewDataSource(liquibaseProperties, dataSourceProperties));
        }
        return liquibase;
    }

    private static DataSource getDataSource(
        DataSource liquibaseDataSource,
        LiquibaseProperties liquibaseProperties,
        DataSource dataSource
    ) {
        if (liquibaseDataSource != null) {
            return liquibaseDataSource;
        }
        if (liquibaseProperties.getUrl() == null && liquibaseProperties.getUser() == null) {
            return dataSource;
        }
        return null;
    }

    private static DataSource createNewDataSource(LiquibaseProperties liquibaseProperties, DataSourceProperties dataSourceProperties) {
        String url = getProperty(liquibaseProperties::getUrl, dataSourceProperties::determineUrl);
        String user = getProperty(liquibaseProperties::getUser, dataSourceProperties::determineUsername);
        String password = getProperty(liquibaseProperties::getPassword, dataSourceProperties::determinePassword);
        return DataSourceBuilder.create().url(url).username(user).password(password).build();
    }

    private static String getProperty(Supplier<String> property, Supplier<String> defaultValue) {
        return Optional.of(property).map(Supplier::get).orElseGet(defaultValue);
    }
}
