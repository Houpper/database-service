package br.com.houpper.database_service.service.impl;

import br.com.houpper.common.exception.exceptions.BadRequestException;
import br.com.houpper.database_service.service.SchemaCreatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implementação da interface {@link SchemaCreatorService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaCreatorServiceImpl implements SchemaCreatorService {

    /**
     * Expressão regular utilizada para validar nomes de schemas.
     * <p>Regras para o nome do schema:</p>
     * <ul>
     *     <li>Deve começar com letra minúscula.</li>
     *     <li>Tamanho deve ser entre 3 e 63 caracteres.</li>
     *     <li>Pode conter apenas letras minúsculas, números e underscore.</li>
     * </ul>
     */
    private static final Pattern SCHEMA_PATTERN = Pattern.compile("^[a-z][a-z0-9_]{2,62}$");

    /**
     * Schemas reservados do PostgreSQL.
     */
    private static final Set<String> RESERVED_SCHEMAS = Set.of(
            "public",
            "pg_catalog",
            "pg_toast",
            "information_schema"
    );

    /**
     * Template utilizado para execução de comandos SQL.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void createSchema(String schemaName) {

        validateSchemaName(schemaName);

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"");

        log.info("Schema '{}' successfully created.", schemaName);
    }

    /**
     * Valida o nome do schema.
     *
     * @param schemaName Nome do schema a ser validado.
     */
    private void validateSchemaName(String schemaName) {

        if (StringUtils.isBlank(schemaName)) {
            throw new BadRequestException("Schema name must not be empty.");
        }

        if (!SCHEMA_PATTERN.matcher(schemaName).matches()) {
            throw new BadRequestException("Invalid schema name.");
        }

        if (RESERVED_SCHEMAS.contains(schemaName)) {
            throw new BadRequestException("Reserved schema name.");
        }

        if (schemaExists(schemaName)) {
            throw new BadRequestException("Schema '" + schemaName + "' already exists.");
        }
    }

    /**
     * Verifica se o schema já existe no banco de dados.
     *
     * @param schemaName nome do schema a ser verificado.
     * @return {@code true} se existir ou {@code false} caso contrário.
     */
    private boolean schemaExists(String schemaName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                            SELECT COUNT(*)
                            FROM information_schema.schemata
                            WHERE schema_name = ?
                        """,
                Integer.class, schemaName);

        return count != null && count > 0;
    }
}