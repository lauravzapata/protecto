package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.exception.DatosInvalidosException;
import co.edu.uniquindio.poo.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SistemaMonederoTest {
    private SistemaMonedero sistema;

    @BeforeEach
    void preparar() throws Exception {
        sistema = new SistemaMonedero();
        sistema.registrarCliente("101", "Ana Pérez", "ana@email.com");
        sistema.registrarCliente("202", "Luis Díaz", "luis@email.com");
        sistema.crearMonedero("101", "Ahorros", TipoMonedero.AHORROS);
        sistema.crearMonedero("202", "Gastos", TipoMonedero.GASTOS_DIARIOS);
    }

    @Test
    void depositoAumentaSaldoYAcumulaPuntos() throws Exception {
        sistema.depositar("101", "MON-1", 1000);
        Cliente ana = sistema.obtenerCliente("101");
        assertEquals(1000.0, ana.getSaldoTotal(), 0.001);
        assertEquals(10, ana.getPuntos());
    }

    @Test
    void retiroNoPermiteSaldoInsuficiente() throws Exception {
        assertThrows(SaldoInsuficienteException.class, () -> sistema.retirar("101", "MON-1", 500));
    }

    @Test
    void transferenciaMueveDineroEntreClientes() throws Exception {
        sistema.depositar("101", "MON-1", 2000);
        sistema.transferir("101", "MON-1", "202", "MON-2", 750);
        assertEquals(1250.0, sistema.obtenerCliente("101").getSaldoTotal(), 0.001);
        assertEquals(750.0, sistema.obtenerCliente("202").getSaldoTotal(), 0.001);
    }

    @Test
    void revertirDepositoActualizaSaldoYPuntos() throws Exception {
        Deposito deposito = sistema.depositar("101", "MON-1", 1000);
        sistema.revertirTransaccion(deposito.getCodigo());
        assertEquals(0.0, sistema.obtenerCliente("101").getSaldoTotal(), 0.001);
        assertEquals(0, sistema.obtenerCliente("101").getPuntos());
    }

    @Test
    void detectaUnMovimientoAtipico() throws Exception {
        for (int i = 0; i < 9; i++) sistema.depositar("101", "MON-1", 100);
        sistema.depositar("101", "MON-1", 10000);
        assertEquals(1, sistema.detectarAtipicas("101").size());
    }

    @Test
    void noPermiteRegistrarDocumentoDuplicado() throws Exception {
        assertThrows(DatosInvalidosException.class,
                () -> sistema.registrarCliente("101", "Otra persona", "otra@email.com"));
    }

    @Test
    void programarDepositoParaHoyLoEjecuta() throws Exception {
        sistema.programar("101", "MON-1", "Depósito", null, null, 500,
                java.time.LocalDate.now(), Frecuencia.UNA_VEZ);
        sistema.procesarProgramadas();
        assertEquals(500.0, sistema.obtenerCliente("101").getSaldoTotal(), 0.001);
    }

    @Test
    void canjearBonoYAplicarloAumentaElSaldo() throws Exception {
        sistema.depositar("101", "MON-1", 100000);
        sistema.canjearBeneficio("101", 3);
        sistema.aplicarBono("101", "MON-1");
        assertEquals(100050.0, sistema.obtenerCliente("101").getSaldoTotal(), 0.001);
        assertEquals(0, sistema.obtenerCliente("101").getPuntos());
    }

    @Test
    void unMovimientoMuySuperiorAlHistorialGeneraAlertaAutomatica() throws Exception {
        sistema.depositar("101", "MON-1", 100);
        sistema.depositar("101", "MON-1", 100);
        sistema.depositar("101", "MON-1", 10000);
        assertEquals(1, sistema.detectarAtipicas("101").size());
        assertTrue(sistema.obtenerCliente("101").getNotificaciones().stream()
                .anyMatch(n -> n.contains("potencialmente atípica")));
    }
}
