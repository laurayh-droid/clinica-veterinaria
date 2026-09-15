package br.com.clinicaveterinaria.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado no banco de dados.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(String.format("%s com ID %d não foi encontrado(a).", recurso, id));
    }
}
