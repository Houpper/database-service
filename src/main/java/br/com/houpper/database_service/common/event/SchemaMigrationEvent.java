package br.com.houpper.database_service.common.event;

import java.util.UUID;

/**
 * Evento publicado para migração de um schema.
 *
 * @param TenantID Identificador do Tenant.
 * @param schemaName Nome do schema a ser migrado.
 */
public record SchemaMigrationEvent(

        UUID TenantID,
        String schemaName
) {
}