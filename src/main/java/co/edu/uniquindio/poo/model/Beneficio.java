package co.edu.uniquindio.poo.model;

public class Beneficio {
    private final String nombre;
    private final String descripcion;
    private final int puntosNecesarios;

    public Beneficio(String nombre, String descripcion, int puntosNecesarios) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosNecesarios = puntosNecesarios;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getPuntosNecesarios() { return puntosNecesarios; }

    @Override
    public String toString() {
        return nombre + " - " + puntosNecesarios + " puntos: " + descripcion;
    }
}
