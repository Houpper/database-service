package br.com.houpper.database_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para consultas relacionadas aos tenants.
 */
@Repository
public class TenantQueryRepository {

    /**
     * Template para execução de consultas SQL.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Cria o repositório de consultas de tenants.
     *
     * @param jdbcTemplate Template JDBC para execução das consultas.
     */
    public TenantQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Verifica se existe um tenant com o ID e o nome do schema informados.
     *
     * @param tenantId   ID do tenant.
     * @param schemaName Nome do schema.
     *
     * @return {@code true} se o tenant existir com o schema informado;
     * {@code false} caso contrário.
     */
    public boolean existsByIdAndSchemaName(UUID tenantId, String schemaName) {
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        """
                                SELECT EXISTS (
                                    SELECT 1
                                    FROM management.tenants
                                    WHERE id = ?
                                      AND schema_name = ?
                                )
                                """,
                        Boolean.class,
                        tenantId,
                        schemaName
                )
        );
    }
}