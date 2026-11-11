package co.edu.uniquindio.poo.exception;

public class DatosInvalidosException extends Exception {
    private static final long serialVersionUID = 1L;

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
