package co.edu.uniquindio.poo.model;

public class Deposito extends Transaccion {
    private final Monedero monedero;
    public Deposito(String codigo, double monto, Cliente cliente, Monedero monedero) {
        super(codigo, monto, cliente);
        this.monedero = monedero;
    }
    public Monedero getMonedero() { return monedero; }
    @Override public String getTipo() { return "Depósito"; }
    @Override public String getDetalle() { return "A " + monedero.getNombre(); }
}
