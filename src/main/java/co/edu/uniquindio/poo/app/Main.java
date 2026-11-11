package co.edu.uniquindio.poo.app;

import co.edu.uniquindio.poo.exception.DatosInvalidosException;
import co.edu.uniquindio.poo.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Main extends Application {
    private final SistemaMonedero sistema = new SistemaMonedero();
    private TextArea resultados;

    @Override
    public void start(Stage escenario) {
        cargarEjemplos();
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(18));
        Label titulo = new Label("MONEDERO PLUS");
        titulo.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #35466D;");
        Label subtitulo = new Label("Simulador de monedero virtual con sistema de puntos");
        VBox encabezado = new VBox(5, titulo, subtitulo);
        encabezado.setPadding(new Insets(5, 5, 18, 5));

        TabPane pestanas = new TabPane();
        pestanas.getTabs().add(crearPestana("Clientes", List.of(
                boton("Registrar cliente", this::registrarCliente),
                boton("Consultar clientes", this::listarClientes))));
        pestanas.getTabs().add(crearPestana("Monederos", List.of(
                boton("Crear monedero", this::crearMonedero),
                boton("Consultar monederos", this::listarMonederos))));
        pestanas.getTabs().add(crearPestana("Movimientos", List.of(
                boton("Depositar", this::depositar),
                boton("Retirar", this::retirar),
                boton("Transferir", this::transferir),
                boton("Ver historial", this::historial),
                boton("Revertir transacción", this::revertir))));
        pestanas.getTabs().add(crearPestana("Puntos y rangos", List.of(
                boton("Consultar puntos y rango", this::consultarPuntos),
                boton("Canjear beneficio", this::canjear),
                boton("Aplicar bono de $50", this::aplicarBono))));
        pestanas.getTabs().add(crearPestana("Programadas", List.of(
                boton("Programar depósito", this::programarDeposito),
                boton("Procesar pendientes", this::procesarProgramadas),
                boton("Consultar programadas", this::listarProgramadas))));
        pestanas.getTabs().add(crearPestana("Reportes y alertas", List.of(
                boton("Reporte de gastos", this::reporte),
                boton("Detectar atípicas", this::atipicas),
                boton("Ver notificaciones", this::notificaciones))));

        resultados = new TextArea("Bienvenida a Monedero Plus. Selecciona una operación.");
        resultados.setEditable(false);
        resultados.setWrapText(true);
        resultados.setPrefRowCount(8);
        VBox contenido = new VBox(12, pestanas, new Label("RESULTADOS"), resultados);
        raiz.setTop(encabezado);
        raiz.setCenter(contenido);
        actualizarTitulo(escenario);
        escenario.setScene(new Scene(raiz, 1000, 700));
        escenario.show();
    }

    private void actualizarTitulo(Stage escenario) { escenario.setTitle("Monedero Plus"); }

    private Tab crearPestana(String nombre, List<Button> botones) {
        FlowPane panel = new FlowPane(10, 10);
        panel.setPadding(new Insets(18));
        panel.getChildren().addAll(botones);
        Tab tab = new Tab(nombre, panel);
        tab.setClosable(false);
        return tab;
    }

    private Button boton(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-background-color: #35466D; -fx-text-fill: white; -fx-padding: 10 15 10 15;");
        boton.setOnAction(e -> ejecutar(accion));
        return boton;
    }

    private void ejecutar(Runnable accion) {
        try { accion.run(); }
        catch (Exception e) { error(e.getMessage()); }
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
        try { return Double.parseDouble(valor.trim().replace(",", ".")); }
        catch (NumberFormatException e) { error("Ingrese un número válido."); return null; }
    }

    private <T> T elegir(String titulo, List<T> opciones) {
        if (opciones.isEmpty()) { error("No hay elementos registrados."); return null; }
        ChoiceDialog<T> dialogo = new ChoiceDialog<>(opciones.get(0), opciones);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);
        dialogo.setContentText("Seleccione:");
        return dialogo.showAndWait().orElse(null);
    }

    private Cliente cliente() { return elegir("Seleccione cliente", sistema.getClientes()); }
    private Monedero monedero(Cliente cliente) { return elegir("Seleccione monedero", cliente.getMonederos()); }

    private void registrarCliente() {
        String documento = texto("Nuevo cliente", "Documento:");
        if (documento == null) return;
        String nombre = texto("Nuevo cliente", "Nombre:");
        if (nombre == null) return;
        String correo = texto("Nuevo cliente", "Correo:");
        if (correo == null) return;
        try { mostrar(sistema.registrarCliente(documento, nombre, correo)); }
        catch (DatosInvalidosException e) { error(e.getMessage()); }
    }

    private void listarClientes() {
        mostrar(sistema.getClientes().isEmpty() ? "No hay clientes." :
                String.join("\n", sistema.getClientes().stream().map(Cliente::toString).toList()));
    }

    private void crearMonedero() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        String nombre = texto("Crear monedero", "Nombre del monedero:");
        if (nombre == null) return;
        TipoMonedero tipo = elegir("Tipo", List.of(TipoMonedero.values()));
        if (tipo == null) return;
        mostrar("Monedero creado: " + sistema.crearMonedero(c.getDocumento(), nombre, tipo));
    }

    private void listarMonederos() {
        StringBuilder salida = new StringBuilder();
        for (Cliente c : sistema.getClientes()) {
            salida.append(c.getNombre()).append("\n");
            for (Monedero m : c.getMonederos()) salida.append("  ").append(m).append("\n");
        }
        mostrar(salida.toString());
    }

    private void depositar() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        Monedero m = monedero(c); if (m == null) return;
        Double valor = monto(); if (valor == null) return;
        mostrar("Depósito realizado: " + sistema.depositar(c.getDocumento(), m.getCodigo(), valor));
    }

    private void retirar() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        Monedero m = monedero(c); if (m == null) return;
        Double valor = monto(); if (valor == null) return;
        mostrar("Retiro realizado: " + sistema.retirar(c.getDocumento(), m.getCodigo(), valor));
    }

    private void transferir() throws Exception {
        Cliente origen = cliente(); if (origen == null) return;
        Monedero mo = monedero(origen); if (mo == null) return;
        Cliente destino = cliente(); if (destino == null) return;
        Monedero md = monedero(destino); if (md == null) return;
        Double valor = monto(); if (valor == null) return;
        mostrar("Transferencia realizada: " + sistema.transferir(origen.getDocumento(), mo.getCodigo(),
                destino.getDocumento(), md.getCodigo(), valor));
    }

    private void historial() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        mostrar(String.join("\n", sistema.historialCliente(c.getDocumento()).stream().map(Transaccion::toString).toList()));
    }

    private void revertir() throws Exception {
        String codigo = texto("Revertir", "Código de la transacción:");
        if (codigo != null) { sistema.revertirTransaccion(codigo); mostrar("Transacción revertida."); }
    }

    private void consultarPuntos() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        mostrar("Cliente: " + c.getNombre() + "\nPuntos: " + c.getPuntos() + "\nRango: " + c.getRango()
                + "\nSaldo total: $" + c.getSaldoTotal());
    }

    private void canjear() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        Integer opcion = elegir("Beneficio", List.of(1, 2, 3));
        if (opcion != null) mostrar(sistema.canjearBeneficio(c.getDocumento(), opcion));
    }

    private void aplicarBono() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        Monedero m = monedero(c); if (m == null) return;
        sistema.aplicarBono(c.getDocumento(), m.getCodigo());
        mostrar("Bono aplicado al monedero " + m.getNombre());
    }

    private void programarDeposito() throws Exception {
        Cliente c = cliente(); if (c == null) return;
        Monedero m = monedero(c); if (m == null) return;
        Double valor = monto(); if (valor == null) return;
        String fechaTexto = texto("Fecha", "Fecha AAAA-MM-DD:");
        if (fechaTexto == null) return;
        LocalDate fecha = LocalDate.parse(fechaTexto);
        Frecuencia frecuencia = elegir("Frecuencia", List.of(Frecuencia.values()));
        if (frecuencia == null) return;
        mostrar("Programada: " + sistema.programar(c.getDocumento(), m.getCodigo(), "Depósito",
                null, null, valor, fecha, frecuencia));
    }

    private void procesarProgramadas() { mostrar(String.join("\n", sistema.procesarProgramadas())); }
    private void listarProgramadas() {
        mostrar(String.join("\n", sistema.getProgramadas().stream().map(TransaccionProgramada::toString).toList()));
    }

    private void reporte() throws Exception {
        Cliente c = cliente(); if (c != null) mostrar(sistema.generarReporteGastos(c.getDocumento()));
    }

    private void atipicas() throws Exception {
        Cliente c = cliente(); if (c != null) mostrar(String.join("\n",
                sistema.detectarAtipicas(c.getDocumento()).stream().map(Transaccion::toString).toList()));
    }

    private void notificaciones() throws Exception {
        Cliente c = cliente(); if (c != null) mostrar(String.join("\n", c.getNotificaciones()));
    }

    private void cargarEjemplos() {
        try {
            sistema.registrarCliente("1001", "Laura Gómez", "laura@email.com");
            sistema.registrarCliente("1002", "Daniel Ruiz", "daniel@email.com");
            sistema.crearMonedero("1001", "Ahorros", TipoMonedero.AHORROS);
            sistema.crearMonedero("1001", "Gastos diarios", TipoMonedero.GASTOS_DIARIOS);
            sistema.crearMonedero("1002", "Ahorros", TipoMonedero.AHORROS);
            sistema.depositar("1001", "MON-1", 150000);
            sistema.depositar("1001", "MON-2", 80000);
            sistema.depositar("1002", "MON-3", 50000);
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    private void mostrar(String mensaje) { resultados.setText(mensaje); }
    private void error(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("No se pudo completar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
