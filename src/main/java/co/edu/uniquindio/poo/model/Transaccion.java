package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;

public abstract class Transaccion {
    private final String codigo;
    private final LocalDateTime fecha;
    private final double monto;
    private final Cliente cliente;
    private boolean activa;
    private boolean sospechosa;

    public Transaccion(String codigo, double monto, Cliente cliente) {
        this.codigo = codigo;
        this.monto = monto;
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.activa = true;
        this.sospechosa = false;
    }

    public String getCodigo() { return codigo; }
    public LocalDateTime getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public Cliente getCliente() { return cliente; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public boolean isSospechosa() { return sospechosa; }
    public void setSospechosa(boolean sospechosa) { this.sospechosa = sospechosa; }
    public abstract String getTipo();
    public abstract String getDetalle();

    @Override
    public String toString() {
        return fecha.toString().replace('T', ' ') + " | " + getTipo() + " | $" + String.format("%,.2f", monto)
                + " | " + getDetalle() + " | " + (activa ? "Activa" : "Revertida")
                + (sospechosa ? " | ALERTA: ATIPICA" : "");
    }
}
