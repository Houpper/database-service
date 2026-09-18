package br.com.houpper.database_service.api.controller;

import br.com.houpper.database_service.common.dto.OperationResponseDTO;
import br.com.houpper.database_service.common.dto.SchemaManagerDTO;
import br.com.houpper.database_service.service.SchemaManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelo gerenciamento dos schemas dos tenants.
 */
@RestController
@RequestMapping("/schema-manager")
@RequiredArgsConstructor
public class SchemaManagerController {

    /**
     * Serviço responsável pelo gerenciamento dos schemas.
     */
    private final SchemaManagerService schemaManagerService;

    /**
     * Cria o schema do tenant e inicia o provisionamento do ambiente.
     *
     * @param request Dados utilizados para identificar o tenant e seu schema.
     * @return Resposta indicando que a solicitação de provisionamento foi aceita.
     */
    @PostMapping("/create")
    public ResponseEntity<OperationResponseDTO> createSchema(@RequestBody SchemaManagerDTO request) {
        this.schemaManagerService.createSchemaAndProvisionEnvironment(request);
        return ResponseEntity.accepted().body(new OperationResponseDTO("Solicitação de provisionamento aceita."));
    }

    /**
     * Inicia a migração do schema do tenant.
     *
     * @param request Dados utilizados para identificar o tenant e seu schema.
     * @return Resposta indicando que a solicitação de migração foi aceita.
     */
    @PostMapping("/migrate")
    public ResponseEntity<OperationResponseDTO> migrateSchema(@RequestBody SchemaManagerDTO request) {
        this.schemaManagerService.migrateSchema(request);
        return ResponseEntity.accepted().body(new OperationResponseDTO("Solicitação de migração aceita."));
    }
}