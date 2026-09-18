package br.com.houpper.database_service.common.listener;

import br.com.houpper.database_service.common.event.SchemaMigrationEvent;
import br.com.houpper.database_service.service.SchemaMigratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener responsável por iniciar a migração do schema.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaMigrationListener {

    /**
     * Serviço responsável por realizar as migrações dos schemas.
     */
    private final SchemaMigratorService schemaMigrator;

    /**
     * Processa o evento de migração do schema.
     *
     * @param event Evento publicado para a migração do schema.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SchemaMigrationEvent event) {

        log.info("Received migration event for schema '{}'.", event.schemaName());

        this.schemaMigrator.performSchemaMigration(event.schemaName());
    }
}