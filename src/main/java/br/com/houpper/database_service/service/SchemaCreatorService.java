package br.com.houpper.database_service.service;

/**
 * Serviço responsável pela criação de schemas no banco de dados.
 */
public interface SchemaCreatorService {

    /**
     * Cria um schema no banco de dados após validar seu nome, garantindo que apenas nomes válidos sejam utilizados em
     * comandos SQL.
     *
     * @param schemaName Nome do schema a ser criado.
     */
    void createSchema(String schemaName);
}