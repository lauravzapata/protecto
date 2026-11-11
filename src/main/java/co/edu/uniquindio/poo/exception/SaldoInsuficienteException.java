package co.edu.uniquindio.poo.exception;

public class SaldoInsuficienteException extends Exception {
    private static final long serialVersionUID = 1L;

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
