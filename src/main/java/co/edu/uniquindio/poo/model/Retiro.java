package co.edu.uniquindio.poo.model;

public class Retiro extends Transaccion {
    private final Monedero monedero;
    public Retiro(String codigo, double monto, Cliente cliente, Monedero monedero) {
        super(codigo, monto, cliente);
        this.monedero = monedero;
    }
    public Monedero getMonedero() { return monedero; }
    @Override public String getTipo() { return "Retiro"; }
    @Override public String getDetalle() { return "Desde " + monedero.getNombre(); }
}
