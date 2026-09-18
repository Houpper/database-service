package br.com.houpper.database_service.service.impl;

import br.com.houpper.database_service.common.dto.SchemaManagerDTO;
import br.com.houpper.database_service.common.event.SchemaMigrationEvent;
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
     * {@inheritDoc}.
     */
    @Override
    @Transactional
    public void createSchemaAndProvisionEnvironment(SchemaManagerDTO request) {

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

        this.publisher.publishEvent(
                new SchemaMigrationEvent(request.tenantID(), request.schemaName())
        );
    }
}