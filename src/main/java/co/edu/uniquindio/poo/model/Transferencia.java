package co.edu.uniquindio.poo.model;

public class Transferencia extends Transaccion {
    private final Monedero origen;
    private final Monedero destino;
    public Transferencia(String codigo, double monto, Cliente cliente, Monedero origen, Monedero destino) {
        super(codigo, monto, cliente);
        this.origen = origen;
        this.destino = destino;
    }
    public Monedero getOrigen() { return origen; }
    public Monedero getDestino() { return destino; }
    @Override public String getTipo() { return "Transferencia"; }
    @Override public String getDetalle() { return origen.getPropietario().getNombre() + " → " + destino.getPropietario().getNombre(); }
}
