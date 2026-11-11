package co.edu.uniquindio.poo.model;

import java.time.LocalDate;

public class TransaccionProgramada {
    private final String codigo;
    private final String tipo;
    private final double monto;
    private final Monedero origen;
    private final Monedero destino;
    private LocalDate fechaEjecucion;
    private final Frecuencia frecuencia;
    private boolean activa;

    public TransaccionProgramada(String codigo, String tipo, double monto, Monedero origen,
                                 Monedero destino, LocalDate fechaEjecucion, Frecuencia frecuencia) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.monto = monto;
        this.origen = origen;
        this.destino = destino;
        this.fechaEjecucion = fechaEjecucion;
        this.frecuencia = frecuencia;
        this.activa = true;
    }
    public String getCodigo() { return codigo; }
    public String getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public Monedero getOrigen() { return origen; }
    public Monedero getDestino() { return destino; }
    public LocalDate getFechaEjecucion() { return fechaEjecucion; }
    public Frecuencia getFrecuencia() { return frecuencia; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public void avanzarFecha() {
        if (frecuencia == Frecuencia.SEMANAL) fechaEjecucion = fechaEjecucion.plusWeeks(1);
        else if (frecuencia == Frecuencia.MENSUAL) fechaEjecucion = fechaEjecucion.plusMonths(1);
        else activa = false;
    }
    @Override public String toString() {
        return codigo + " | " + tipo + " | $" + String.format("%,.2f", monto) + " | " + fechaEjecucion
                + " | " + frecuencia + " | " + (activa ? "Activa" : "Finalizada");
    }
}
