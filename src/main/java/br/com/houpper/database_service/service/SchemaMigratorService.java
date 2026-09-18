package br.com.houpper.database_service.service;

/**
 * Serviço responsável pela execução das migrações dos schemas utilizando o Flyway.
 */
public interface SchemaMigratorService {

    /**
     * Executa de forma assíncrona as migrações Flyway do schema informado.
     *
     * @param schemaName Nome do schema a ser migrado.
     */
    void performSchemaMigration(String schemaName);
}