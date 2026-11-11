package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.interfaces.INotificable;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements INotificable {
    private final String documento;
    private String nombre;
    private String correo;
    private final List<Monedero> monederos;
    private final List<MovimientoPuntos> movimientosPuntos;
    private final List<String> notificaciones;
    private final List<Beneficio> beneficiosCanjeados;

    public Cliente(String documento, String nombre, String correo) {
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
        this.monederos = new ArrayList<>();
        this.movimientosPuntos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.beneficiosCanjeados = new ArrayList<>();
    }

    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public List<Monedero> getMonederos() { return monederos; }
    public List<MovimientoPuntos> getMovimientosPuntos() { return movimientosPuntos; }
    public List<String> getNotificaciones() { return notificaciones; }
    public List<Beneficio> getBeneficiosCanjeados() { return beneficiosCanjeados; }

    public int getPuntos() {
        return movimientosPuntos.stream().filter(MovimientoPuntos::isActivo)
                .mapToInt(MovimientoPuntos::getPuntos).sum();
    }

    public String getRango() {
        int puntos = getPuntos();
        if (puntos <= 500) return "Bronce";
        if (puntos <= 1000) return "Plata";
        if (puntos <= 5000) return "Oro";
        return "Platino";
    }

    public double getSaldoTotal() {
        return monederos.stream().mapToDouble(Monedero::getSaldo).sum();
    }

    @Override
    public void recibirNotificacion(String mensaje) {
        notificaciones.add(java.time.LocalDateTime.now() + " - " + mensaje);
    }

    @Override
    public String toString() {
        return documento + " - " + nombre + " | " + correo;
    }
}
