package br.com.houpper.database_service.service.impl;

import br.com.houpper.database_service.service.SchemaMigratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Implementação da interface {@link SchemaMigratorService}.
 * <p>
 * Serviço responsável por executar as migrações Flyway dos schemas. Cada execução cria uma instância isolada do
 * Flyway configurada para o schema informado, garantindo que apenas as migrações destinadas ao schema sejam aplicadas.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaMigratorServiceImpl implements SchemaMigratorService {

    /**
     * Fonte de dados utilizada pelo Flyway.
     */
    private final DataSource dataSource;

    /**
     * Localização das migrations dos tenants.
     */
    private static final String MIGRATION_LOCATION = "classpath:db/migration/tenant";

    /**
     * {@inheritDoc}
     */
    @Async("migrationTaskExecutor")
    @Override
    public void performSchemaMigration(String schemaName) {

        try {
            long start = System.currentTimeMillis();

            Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations(MIGRATION_LOCATION)
                    .baselineOnMigrate(true)
                    .cleanDisabled(true)
                    .load()
                    .migrate();

            log.info("Flyway migration successfully completed for schema '{}' in {} ms.",
                    schemaName, System.currentTimeMillis() - start);

        } catch (FlywayException ex) {
            log.error("Flyway migration failed for schema '{}'.", schemaName, ex);
            throw ex;
        }
    }
}