package br.com.houpper.database_service.service.impl;

import br.com.houpper.common.exception.exceptions.NotFoundException;
import br.com.houpper.database_service.common.dto.SchemaManagerDTO;
import br.com.houpper.database_service.common.event.SchemaMigrationEvent;
import br.com.houpper.database_service.repository.TenantQueryRepository;
import br.com.houpper.database_service.service.SchemaCreatorService;
import br.com.houpper.database_service.service.SchemaManagerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Implementação da interface {@link SchemaManagerService}.
 */
@Service
@RequiredArgsConstructor
public class SchemaManagerServiceImpl implements SchemaManagerService {

    /**
     * Serviço responsável pela criação do schema.
     */
    private final SchemaCreatorService schemaCreator;

    /**
     * Publicador de eventos da aplicação.
     */
    private final ApplicationEventPublisher publisher;

    /**
     * Repositório responsável pelas consultas de tenants no banco de dados.
     */
    private final TenantQueryRepository tenantRepository;

    /**
     * {@inheritDoc}.
     */
    @Override
    @Transactional
    public void createSchemaAndProvisionEnvironment(SchemaManagerDTO request) {

        validateTenant(request);

        this.schemaCreator.createSchema(request.schemaName());

        this.publisher.publishEvent(
                new SchemaMigrationEvent(request.tenantID(), request.schemaName())
        );
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @Transactional
    public void migrateSchema(SchemaManagerDTO request) {

        validateTenant(request);

        this.publisher.publishEvent(
                new SchemaMigrationEvent(request.tenantID(), request.schemaName())
        );
    }

    /**
     * Valida a existência do tenant e a associação com o schema informado.
     *
     * @param request Dados do tenant e do schema.
     *
     * @throws NotFoundException Se o tenant não existir ou o schema não estiver associado ao tenant.
     */
    private void validateTenant(SchemaManagerDTO request) {

        boolean isValidTenant =
                tenantRepository.existsByIdAndSchemaName(request.tenantID(), request.schemaName());

        if (!isValidTenant) {
            throw new NotFoundException(
                    "Tenant não encontrado para os parâmetros informados.");
        }
    }
}