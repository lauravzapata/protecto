package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.List;

public class Monedero {
    private final String codigo;
    private String nombre;
    private final TipoMonedero tipo;
    private double saldo;
    private final Cliente propietario;
    private final List<Transaccion> historial;

    public Monedero(String codigo, String nombre, TipoMonedero tipo, Cliente propietario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.propietario = propietario;
        this.saldo = 0;
        this.historial = new ArrayList<>();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoMonedero getTipo() { return tipo; }
    public double getSaldo() { return saldo; }
    public Cliente getPropietario() { return propietario; }
    public List<Transaccion> getHistorial() { return historial; }
    public void aumentarSaldo(double monto) { saldo += monto; }
    public void disminuirSaldo(double monto) { saldo -= monto; }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + tipo + ") | Saldo: $" + String.format("%,.2f", saldo);
    }
}
