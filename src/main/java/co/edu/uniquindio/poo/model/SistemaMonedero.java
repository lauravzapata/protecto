package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.exception.ClienteNoEncontradoException;
import co.edu.uniquindio.poo.exception.DatosInvalidosException;
import co.edu.uniquindio.poo.exception.SaldoInsuficienteException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SistemaMonedero {
    private final List<Cliente> clientes;
    private final List<Transaccion> transacciones;
    private final List<TransaccionProgramada> programadas;
    private int consecutivo;
    private static final double SALDO_BAJO = 10000;

    public SistemaMonedero() {
        clientes = new ArrayList<>();
        transacciones = new ArrayList<>();
        programadas = new ArrayList<>();
        consecutivo = 1;
    }

    public List<Cliente> getClientes() { return clientes; }
    public List<Transaccion> getTransacciones() { return transacciones; }
    public List<TransaccionProgramada> getProgramadas() { return programadas; }

    public String registrarCliente(String documento, String nombre, String correo) throws DatosInvalidosException {
        validarTexto(documento, "El documento");
        validarTexto(nombre, "El nombre");
        validarTexto(correo, "El correo");
        if (!correo.contains("@") || !correo.contains(".")) throw new DatosInvalidosException("Ingrese un correo válido.");
        if (buscarCliente(documento).isPresent()) throw new DatosInvalidosException("Ya existe un cliente con ese documento.");
        clientes.add(new Cliente(documento.trim(), nombre.trim(), correo.trim()));
        return "Cliente registrado correctamente.";
    }

    public Optional<Cliente> buscarCliente(String documento) {
        return clientes.stream().filter(c -> c.getDocumento().equalsIgnoreCase(documento.trim())).findFirst();
    }

    public Cliente obtenerCliente(String documento) throws ClienteNoEncontradoException {
        return buscarCliente(documento).orElseThrow(() -> new ClienteNoEncontradoException("No se encontró un cliente con documento " + documento + "."));
    }

    public Monedero crearMonedero(String documento, String nombre, TipoMonedero tipo)
            throws ClienteNoEncontradoException, DatosInvalidosException {
        validarTexto(nombre, "El nombre del monedero");
        if (tipo == null) throw new DatosInvalidosException("Seleccione el tipo de monedero.");
        Cliente cliente = obtenerCliente(documento);
        String codigo = "MON-" + consecutivo++;
        Monedero monedero = new Monedero(codigo, nombre.trim(), tipo, cliente);
        cliente.getMonederos().add(monedero);
        return monedero;
    }

    public Monedero obtenerMonedero(Cliente cliente, String codigo) throws DatosInvalidosException {
        return cliente.getMonederos().stream().filter(m -> m.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst().orElseThrow(() -> new DatosInvalidosException("El cliente no tiene ese monedero."));
    }

    public Deposito depositar(String documento, String codigoMonedero, double monto)
            throws ClienteNoEncontradoException, DatosInvalidosException {
        validarMonto(monto);
        Cliente cliente = obtenerCliente(documento);
        Monedero monedero = obtenerMonedero(cliente, codigoMonedero);
        Deposito deposito = new Deposito(nuevoCodigo("DEP"), monto, cliente, monedero);
        monedero.aumentarSaldo(monto);
        guardarTransaccion(deposito, monedero, 1);
        notificarSaldoBajo(cliente, monedero);
        return deposito;
    }

    public Retiro retirar(String documento, String codigoMonedero, double monto)
            throws ClienteNoEncontradoException, DatosInvalidosException, SaldoInsuficienteException {
        validarMonto(monto);
        Cliente cliente = obtenerCliente(documento);
        Monedero monedero = obtenerMonedero(cliente, codigoMonedero);
        if (monedero.getSaldo() < monto) throw new SaldoInsuficienteException("El saldo del monedero no es suficiente.");
        Retiro retiro = new Retiro(nuevoCodigo("RET"), monto, cliente, monedero);
        monedero.disminuirSaldo(monto);
        guardarTransaccion(retiro, monedero, 2);
        notificarSaldoBajo(cliente, monedero);
        return retiro;
    }

    public Transferencia transferir(String documentoOrigen, String codigoOrigen, String documentoDestino, String codigoDestino, double monto)
            throws ClienteNoEncontradoException, DatosInvalidosException, SaldoInsuficienteException {
        validarMonto(monto);
        Cliente clienteOrigen = obtenerCliente(documentoOrigen);
        Cliente clienteDestino = obtenerCliente(documentoDestino);
        Monedero origen = obtenerMonedero(clienteOrigen, codigoOrigen);
        Monedero destino = obtenerMonedero(clienteDestino, codigoDestino);
        if (origen == destino) throw new DatosInvalidosException("Seleccione dos monederos diferentes.");
        if (origen.getSaldo() < monto) throw new SaldoInsuficienteException("El saldo del monedero origen no es suficiente.");
        Transferencia transferencia = new Transferencia(nuevoCodigo("TRA"), monto, clienteOrigen, origen, destino);
        origen.disminuirSaldo(monto);
        destino.aumentarSaldo(monto);
        guardarTransaccion(transferencia, origen, 3);
        destino.getHistorial().add(transferencia);
        notificarSaldoBajo(clienteOrigen, origen);
        clienteDestino.recibirNotificacion("Recibió una transferencia de $" + String.format("%,.2f", monto) + ".");
        return transferencia;
    }

    private void guardarTransaccion(Transaccion transaccion, Monedero monedero, int puntosPorCadaCien) {
        transacciones.add(transaccion);
        monedero.getHistorial().add(transaccion);
        int puntos = ((int) (transaccion.getMonto() / 100)) * puntosPorCadaCien;
        if (puntos > 0) transaccion.getCliente().getMovimientosPuntos().add(new MovimientoPuntos("Puntos por " + transaccion.getTipo(), puntos, transaccion));
        transaccion.getCliente().recibirNotificacion(transaccion.getTipo() + " realizado por $" + String.format("%,.2f", transaccion.getMonto()) + ".");
        analizarMovimientoAtipico(transaccion.getCliente(), transaccion);
        if (transaccion instanceof Transferencia) {
            Transferencia transferencia = (Transferencia) transaccion;
            analizarMovimientoAtipico(transferencia.getDestino().getPropietario(), transaccion);
        }
    }

    private void analizarMovimientoAtipico(Cliente cliente, Transaccion nueva) {
        List<Transaccion> anteriores = transacciones.stream().filter(t -> t != nueva && t.isActiva())
                .filter(t -> perteneceAlHistorial(t, cliente)).toList();
        if (anteriores.size() < 2) return;
        double promedio = anteriores.stream().mapToDouble(Transaccion::getMonto).average().orElse(0);
        double varianza = anteriores.stream().mapToDouble(t -> Math.pow(t.getMonto() - promedio, 2)).average().orElse(0);
        double desviacion = Math.sqrt(varianza);
        if (nueva.getMonto() > promedio + 2 * desviacion) {
            nueva.setSospechosa(true);
            cliente.recibirNotificacion("Alerta: la transacción " + nueva.getCodigo() + " por $" + String.format("%,.2f", nueva.getMonto()) + " es potencialmente atípica.");
        }
    }

    public void revertirTransaccion(String codigo) throws DatosInvalidosException, SaldoInsuficienteException {
        Transaccion transaccion = transacciones.stream().filter(t -> t.getCodigo().equalsIgnoreCase(codigo.trim())).findFirst()
                .orElseThrow(() -> new DatosInvalidosException("No existe una transacción con ese código."));
        if (!transaccion.isActiva()) throw new DatosInvalidosException("La transacción ya fue revertida.");
        int puntosAsociados = transaccion.getCliente().getMovimientosPuntos().stream()
                .filter(m -> m.getTransaccion() == transaccion && m.isActivo()).mapToInt(MovimientoPuntos::getPuntos).sum();
        if (transaccion.getCliente().getPuntos() < puntosAsociados) throw new DatosInvalidosException("No se puede revertir porque los puntos de esa transacción ya fueron utilizados.");

        if (transaccion instanceof Deposito) {
            Deposito deposito = (Deposito) transaccion;
            if (deposito.getMonedero().getSaldo() < transaccion.getMonto()) throw new SaldoInsuficienteException("No se puede revertir: el dinero de ese depósito ya fue utilizado.");
            deposito.getMonedero().disminuirSaldo(transaccion.getMonto());
        } else if (transaccion instanceof Retiro) {
            Retiro retiro = (Retiro) transaccion;
            retiro.getMonedero().aumentarSaldo(transaccion.getMonto());
        } else if (transaccion instanceof Transferencia) {
            Transferencia transferencia = (Transferencia) transaccion;
            if (transferencia.getDestino().getSaldo() < transaccion.getMonto()) throw new SaldoInsuficienteException("No se puede revertir: el monedero destino ya utilizó ese dinero.");
            transferencia.getDestino().disminuirSaldo(transaccion.getMonto());
            transferencia.getOrigen().aumentarSaldo(transaccion.getMonto());
        }
        transaccion.setActiva(false);
        transaccion.getCliente().getMovimientosPuntos().stream().filter(m -> m.getTransaccion() == transaccion).forEach(m -> m.setActivo(false));
        transaccion.getCliente().recibirNotificacion("La transacción " + codigo + " fue revertida. Sus puntos fueron recalculados.");
    }

    public String canjearBeneficio(String documento, int opcion) throws ClienteNoEncontradoException, DatosInvalidosException {
        Cliente cliente = obtenerCliente(documento);
        Beneficio beneficio;
        if (opcion == 1) beneficio = new Beneficio("Descuento en transferencias", "Reducción del 10% en la comisión por transferencias futuras.", 100);
        else if (opcion == 2) beneficio = new Beneficio("Retiros sin cargos", "Un mes sin cargos por retiros.", 500);
        else if (opcion == 3) beneficio = new Beneficio("Bono de saldo", "Bono de 50 unidades en el monedero seleccionado.", 1000);
        else throw new DatosInvalidosException("Seleccione un beneficio válido.");
        if (cliente.getPuntos() < beneficio.getPuntosNecesarios()) throw new DatosInvalidosException("No tiene puntos suficientes para este beneficio.");
        cliente.getMovimientosPuntos().add(new MovimientoPuntos("Canje: " + beneficio.getNombre(), -beneficio.getPuntosNecesarios(), null));
        cliente.getBeneficiosCanjeados().add(beneficio);
        cliente.recibirNotificacion("Canjeó el beneficio: " + beneficio.getNombre() + ".");
        return "Beneficio canjeado: " + beneficio.getNombre() + ".";
    }

    public void aplicarBono(String documento, String codigoMonedero) throws ClienteNoEncontradoException, DatosInvalidosException {
        Cliente cliente = obtenerCliente(documento);
        Beneficio bono = cliente.getBeneficiosCanjeados().stream().filter(b -> b.getNombre().equals("Bono de saldo")).findFirst()
                .orElseThrow(() -> new DatosInvalidosException("El cliente no tiene un bono de saldo pendiente."));
        Monedero monedero = obtenerMonedero(cliente, codigoMonedero);
        monedero.aumentarSaldo(50);
        cliente.getBeneficiosCanjeados().remove(bono);
        cliente.recibirNotificacion("Se aplicó un bono de $50 a " + monedero.getNombre() + ".");
    }

    public TransaccionProgramada programar(String documento, String codigoOrigen, String tipo, String documentoDestino, String codigoDestino,
                                           double monto, LocalDate fecha, Frecuencia frecuencia)
            throws ClienteNoEncontradoException, DatosInvalidosException {
        validarMonto(monto);
        if (fecha == null) throw new DatosInvalidosException("La fecha de ejecución es obligatoria.");
        if (frecuencia == null) throw new DatosInvalidosException("Seleccione la frecuencia.");
        if (tipo == null || (!tipo.equals("Depósito") && !tipo.equals("Transferencia"))) throw new DatosInvalidosException("El tipo programado debe ser Depósito o Transferencia.");
        if (fecha.isBefore(LocalDate.now())) throw new DatosInvalidosException("La fecha debe ser hoy o posterior.");
        Cliente cliente = obtenerCliente(documento);
        Monedero origen = obtenerMonedero(cliente, codigoOrigen);
        Monedero destino = null;
        if (tipo.equals("Transferencia")) {
            Cliente receptor = obtenerCliente(documentoDestino);
            destino = obtenerMonedero(receptor, codigoDestino);
            if (origen == destino) throw new DatosInvalidosException("El origen y destino deben ser diferentes.");
        }
        TransaccionProgramada programada = new TransaccionProgramada(nuevoCodigo("PRO"), tipo, monto, origen, destino, fecha, frecuencia);
        programadas.add(programada);
        return programada;
    }

    public List<String> procesarProgramadas() {
        List<String> resultados = new ArrayList<>();
        List<TransaccionProgramada> pendientes = programadas.stream().filter(p -> p.isActiva())
                .filter(p -> !p.getFechaEjecucion().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(TransaccionProgramada::getFechaEjecucion)).toList();
        for (TransaccionProgramada p : pendientes) {
            try {
                if (p.getTipo().equals("Depósito")) {
                    Cliente cliente = p.getOrigen().getPropietario();
                    depositar(cliente.getDocumento(), p.getOrigen().getCodigo(), p.getMonto());
                } else if (p.getTipo().equals("Transferencia")) {
                    transferir(p.getOrigen().getPropietario().getDocumento(), p.getOrigen().getCodigo(),
                            p.getDestino().getPropietario().getDocumento(), p.getDestino().getCodigo(), p.getMonto());
                }
                resultados.add(p.getCodigo() + ": ejecutada correctamente.");
                p.getOrigen().getPropietario().recibirNotificacion("La transacción programada " + p.getCodigo() + " se ejecutó correctamente.");
                p.avanzarFecha();
            } catch (Exception e) {
                resultados.add(p.getCodigo() + ": no se pudo ejecutar. " + e.getMessage());
                p.getOrigen().getPropietario().recibirNotificacion("No se pudo ejecutar la transacción programada " + p.getCodigo() + ": " + e.getMessage());
                p.setActiva(false);
            }
        }
        return resultados;
    }

    public List<Transaccion> historialCliente(String documento) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(documento);
        return transacciones.stream().filter(t -> perteneceAlHistorial(t, cliente))
                .sorted(Comparator.comparing(Transaccion::getFecha).reversed()).toList();
    }

    private boolean perteneceAlHistorial(Transaccion transaccion, Cliente cliente) {
        if (transaccion.getCliente() == cliente) return true;
        if (transaccion instanceof Transferencia) return ((Transferencia) transaccion).getDestino().getPropietario() == cliente;
        return false;
    }

    public String generarReporteGastos(String documento) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(documento);
        List<Transaccion> gastos = transacciones.stream().filter(Transaccion::isActiva).filter(t -> t.getCliente() == cliente)
                .filter(t -> t instanceof Retiro || t instanceof Transferencia).toList();
        double total = gastos.stream().mapToDouble(Transaccion::getMonto).sum();
        String porTipo = gastos.stream().collect(Collectors.groupingBy(Transaccion::getTipo, Collectors.summingDouble(Transaccion::getMonto))).toString();
        return "REPORTE DE GASTOS\nCliente: " + cliente.getNombre() + "\nCantidad de movimientos: " + gastos.size()
                + "\nTotal de gastos: $" + String.format("%,.2f", total) + "\nGastos por tipo: " + porTipo;
    }

    public List<Transaccion> detectarAtipicas(String documento) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(documento);
        return transacciones.stream().filter(Transaccion::isActiva).filter(Transaccion::isSospechosa)
                .filter(t -> perteneceAlHistorial(t, cliente)).toList();
    }

    public double calcularPromedioGastos(String documento) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(documento);
        return transacciones.stream().filter(Transaccion::isActiva).filter(t -> t.getCliente() == cliente)
                .filter(t -> t instanceof Retiro || t instanceof Transferencia).mapToDouble(Transaccion::getMonto).average().orElse(0);
    }

    private void notificarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < SALDO_BAJO) cliente.recibirNotificacion("Alerta de saldo bajo: " + monedero.getNombre()
                + " tiene $" + String.format("%,.2f", monedero.getSaldo()) + ".");
    }

    private String nuevoCodigo(String prefijo) { return prefijo + "-" + consecutivo++; }
    private void validarMonto(double monto) throws DatosInvalidosException {
        if (!Double.isFinite(monto) || monto <= 0) throw new DatosInvalidosException("El monto debe ser un número mayor que cero.");
    }
    private void validarTexto(String texto, String campo) throws DatosInvalidosException {
        if (texto == null || texto.trim().isEmpty()) throw new DatosInvalidosException(campo + " es obligatorio.");
    }
}
