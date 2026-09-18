package br.com.houpper.database_service.common.dto;

/**
 * DTO utilizado para representar a resposta de uma operação.
 *
 * @param message Mensagem descritiva sobre o resultado da operação.
 */
public record OperationResponseDTO(
        String message
) {
}