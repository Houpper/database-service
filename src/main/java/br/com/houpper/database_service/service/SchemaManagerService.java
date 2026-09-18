package br.com.houpper.database_service.service;

import br.com.houpper.database_service.common.dto.SchemaManagerDTO;

/**
 * Serviço responsável por coordenar a criação e a migração dos schemas dos tenants.
 */
public interface SchemaManagerService {

    /**
     * Cria o schema do tenant e inicia o provisionamento do ambiente.
     *
     * @param request dados utilizados para identificar o tenant e seu schema.
     */
    void createSchemaAndProvisionEnvironment(SchemaManagerDTO request);

    /**
     * Inicia a migração do schema do tenant.
     *
     * @param request Dados utilizados para identificar o tenant e seu schema.
     */
    void migrateSchema(SchemaManagerDTO request);
}