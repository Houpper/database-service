package br.com.houpper.database_service.common.dto;

import java.util.UUID;

/**
 * Dados utilizados para identificar o schema de um tenant.
 *
 * @param tenantID   Identificador do tenant.
 * @param schemaName Nome do schema do tenant.
 */
public record SchemaManagerDTO(
        UUID tenantID,
        String schemaName
) {
}