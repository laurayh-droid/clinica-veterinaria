package br.com.clinicaveterinaria.exception;

/**
 * Exceção lançada quando uma regra de negócio ou validação de domínio é violada.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String message) {
        super(message);
    }
}
