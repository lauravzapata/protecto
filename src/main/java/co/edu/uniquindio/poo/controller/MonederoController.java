package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoMonedero;

/**
 * Controlador de la aplicación. Centraliza el acceso a las operaciones del modelo.
 */
public class MonederoController {

    private final SistemaMonedero sistema;

    public MonederoController() {
        sistema = new SistemaMonedero();
        cargarDatosIniciales();
    }

    public SistemaMonedero getSistema() {
        return sistema;
    }

    private void cargarDatosIniciales() {
        try {
            sistema.registrarCliente("1001", "Laura Gómez", "laura@email.com");
            sistema.registrarCliente("1002", "Daniel Ruiz", "daniel@email.com");
            sistema.crearMonedero("1001", "Ahorros", TipoMonedero.AHORROS);
            sistema.crearMonedero("1001", "Gastos diarios", TipoMonedero.GASTOS_DIARIOS);
            sistema.crearMonedero("1002", "Ahorros", TipoMonedero.AHORROS);
            sistema.depositar("1001", "MON-1", 150000);
            sistema.depositar("1001", "MON-2", 80000);
            sistema.depositar("1002", "MON-3", 50000);
        } catch (Exception e) {
            System.err.println("No se pudieron cargar los datos iniciales: " + e.getMessage());
        }
    }
}