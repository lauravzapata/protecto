package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;

public class MovimientoPuntos {
    private final String descripcion;
    private final int puntos;
    private final LocalDateTime fecha;
    private final Transaccion transaccion;
    private boolean activo;

    public MovimientoPuntos(String descripcion, int puntos, Transaccion transaccion) {
        this.descripcion = descripcion;
        this.puntos = puntos;
        this.transaccion = transaccion;
        this.fecha = LocalDateTime.now();
        this.activo = true;
    }

    public String getDescripcion() { return descripcion; }
    public int getPuntos() { return puntos; }
    public LocalDateTime getFecha() { return fecha; }
    public Transaccion getTransaccion() { return transaccion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
