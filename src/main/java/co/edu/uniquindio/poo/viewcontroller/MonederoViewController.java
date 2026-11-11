package co.edu.uniquindio.poo.viewcontroller;

import co.edu.uniquindio.poo.controller.MonederoController;
import co.edu.uniquindio.poo.exception.DatosInvalidosException;
import co.edu.uniquindio.poo.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controla los componentes visuales definidos en Monedero.fxml.
 */
public class MonederoViewController {

    @FXML private TextArea resultados;
    private final MonederoController controller = new MonederoController();

    private void mostrar(String mensaje) {
        resultados.setText(mensaje == null || mensaje.isEmpty() ? "No hay información para mostrar." : mensaje);
    }

    private void error(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private String texto(String titulo, String pregunta) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);
        dialogo.setContentText(pregunta);
        return dialogo.showAndWait().orElse(null);
    }

    private Double monto() {
        String valor = texto("Monto", "Ingrese el monto:");
        if (valor == null) return null;
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            error("Ingrese un número válido.");
            return null;
        }
    }

    private <T> T elegir(String titulo, List<T> opciones) {
        if (opciones == null || opciones.isEmpty()) {
            error("No hay elementos registrados.");
            return null;
        }
        ChoiceDialog<T> dialogo = new ChoiceDialog<>(opciones.get(0), opciones);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);
        dialogo.setContentText("Seleccione:");
        return dialogo.showAndWait().orElse(null);
    }

    private SistemaMonedero sistema() { return controller.getSistema(); }
    private Cliente cliente() { return elegir("Seleccione cliente", sistema().getClientes()); }
    private Monedero monedero(Cliente cliente) { return elegir("Seleccione monedero", cliente.getMonederos()); }

    @FXML private void registrarCliente() {
        String documento = texto("Nuevo cliente", "Documento:");
        if (documento == null) return;
        String nombre = texto("Nuevo cliente", "Nombre:");
        if (nombre == null) return;
        String correo = texto("Nuevo cliente", "Correo:");
        if (correo == null) return;
        try { mostrar(sistema().registrarCliente(documento, nombre, correo)); }
        catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void listarClientes() {
        mostrar(sistema().getClientes().isEmpty() ? "No hay clientes." :
                String.join("\n", sistema().getClientes().stream().map(Cliente::toString).toList()));
    }

    @FXML private void crearMonedero() {
        try {
            Cliente c = cliente(); if (c == null) return;
            String nombre = texto("Crear monedero", "Nombre del monedero:");
            if (nombre == null) return;
            TipoMonedero tipo = elegir("Tipo", List.of(TipoMonedero.values()));
            if (tipo == null) return;
            mostrar("Monedero creado: " + sistema().crearMonedero(c.getDocumento(), nombre, tipo));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void listarMonederos() {
        StringBuilder salida = new StringBuilder();
        for (Cliente c : sistema().getClientes()) {
            salida.append(c.getNombre()).append("\n");
            for (Monedero m : c.getMonederos()) salida.append("  ").append(m).append("\n");
        }
        mostrar(salida.toString());
    }

    @FXML private void depositar() {
        try {
            Cliente c = cliente(); if (c == null) return;
            Monedero m = monedero(c); if (m == null) return;
            Double valor = monto(); if (valor == null) return;
            mostrar("Depósito realizado: " + sistema().depositar(c.getDocumento(), m.getCodigo(), valor));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void retirar() {
        try {
            Cliente c = cliente(); if (c == null) return;
            Monedero m = monedero(c); if (m == null) return;
            Double valor = monto(); if (valor == null) return;
            mostrar("Retiro realizado: " + sistema().retirar(c.getDocumento(), m.getCodigo(), valor));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void transferir() {
        try {
            Cliente origen = cliente(); if (origen == null) return;
            Monedero mo = monedero(origen); if (mo == null) return;
            Cliente destino = cliente(); if (destino == null) return;
            Monedero md = monedero(destino); if (md == null) return;
            Double valor = monto(); if (valor == null) return;
            mostrar("Transferencia realizada: " + sistema().transferir(origen.getDocumento(), mo.getCodigo(),
                    destino.getDocumento(), md.getCodigo(), valor));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void historial() {
        try {
            Cliente c = cliente(); if (c == null) return;
            mostrar(String.join("\n", sistema().historialCliente(c.getDocumento()).stream()
                    .map(Transaccion::toString).toList()));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void revertir() {
        try {
            String codigo = texto("Revertir", "Código de la transacción:");
            if (codigo != null) { sistema().revertirTransaccion(codigo); mostrar("Transacción revertida."); }
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void consultarPuntos() {
        try {
            Cliente c = cliente(); if (c == null) return;
            mostrar("Cliente: " + c.getNombre() + "\nPuntos: " + c.getPuntos() + "\nRango: " + c.getRango()
                    + "\nSaldo total: $" + c.getSaldoTotal());
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void canjear() {
        try {
            Cliente c = cliente(); if (c == null) return;
            Integer opcion = elegir("Beneficio", List.of(1, 2, 3));
            if (opcion != null) mostrar(sistema().canjearBeneficio(c.getDocumento(), opcion));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void aplicarBono() {
        try {
            Cliente c = cliente(); if (c == null) return;
            Monedero m = monedero(c); if (m == null) return;
            sistema().aplicarBono(c.getDocumento(), m.getCodigo());
            mostrar("Bono aplicado al monedero " + m.getNombre());
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void programarDeposito() {
        try {
            Cliente c = cliente(); if (c == null) return;
            Monedero m = monedero(c); if (m == null) return;
            Double valor = monto(); if (valor == null) return;
            String fechaTexto = texto("Fecha", "Fecha AAAA-MM-DD:");
            if (fechaTexto == null) return;
            LocalDate fecha = LocalDate.parse(fechaTexto);
            Frecuencia frecuencia = elegir("Frecuencia", List.of(Frecuencia.values()));
            if (frecuencia == null) return;
            mostrar("Programada: " + sistema().programar(c.getDocumento(), m.getCodigo(), "Depósito",
                    null, null, valor, fecha, frecuencia));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void procesarProgramadas() {
        mostrar(String.join("\n", sistema().procesarProgramadas()));
    }

    @FXML private void listarProgramadas() {
        mostrar(String.join("\n", sistema().getProgramadas().stream()
                .map(TransaccionProgramada::toString).toList()));
    }

    @FXML private void reporte() {
        try {
            Cliente c = cliente();
            if (c != null) mostrar(sistema().generarReporteGastos(c.getDocumento()));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void atipicas() {
        try {
            Cliente c = cliente(); if (c == null) return;
            mostrar(String.join("\n", sistema().detectarAtipicas(c.getDocumento()).stream()
                    .map(Transaccion::toString).toList()));
        } catch (Exception e) { error(e.getMessage()); }
    }

    @FXML private void notificaciones() {
        Cliente c = cliente();
        if (c != null) mostrar(String.join("\n", c.getNotificaciones()));
    }
}