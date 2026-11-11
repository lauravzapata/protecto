# Guía de sustentación — Monedero Plus

## ¿Qué hace?
Simula un monedero virtual. Permite registrar clientes, crear monederos, depositar, retirar, transferir, consultar movimientos, acumular puntos, canjear beneficios y programar depósitos.

## Organización
- app/Main.java: ventanas, pestañas y botones de JavaFX.
- model: clases que representan clientes, monederos y transacciones.
- model/SistemaMonedero.java: reglas y operaciones principales.
- exception: errores personalizados.
- interfaces/INotificable.java: contrato para recibir notificaciones.

## POO
**Abstracción:** Transaccion es abstracta porque representa un movimiento general.

**Encapsulamiento:** los atributos son privados y se consultan o modifican por métodos.

**Herencia:** Deposito, Retiro y Transferencia heredan de Transaccion.

**Polimorfismo:** cada clase hija implementa getTipo() y getDetalle() de forma diferente.

**Interfaz:** Cliente implementa INotificable y su método recibirNotificacion.

## Puntos
Por cada 100 unidades completas: depósito 1 punto, retiro 2 puntos y transferencia 3 puntos. Los rangos son Bronce, Plata, Oro y Platino según los límites del enunciado.

## Detección atípica
Se calcula promedio y desviación estándar poblacional de los movimientos anteriores. Si el nuevo monto supera promedio + 2 × desviación estándar, se marca como atípico. El costo temporal es O(n), porque se recorren los movimientos una cantidad constante de veces.

## Stream API
Se usan filter para filtrar, findFirst para encontrar, mapToDouble y sum para calcular, sorted para ordenar y groupingBy para agrupar. Las expresiones lambda definen las condiciones o acciones.

## Prueba de demostración
Al iniciar se cargan dos clientes de ejemplo:
- Documento 1001, Laura Gómez: MON-1 Ahorros y MON-2 Gastos diarios.
- Documento 1002, Daniel Ruiz: MON-3 Ahorros.

Saldos iniciales: MON-1 $150.000, MON-2 $80.000 y MON-3 $50.000.

## Limitaciones que debo explicar
- Es una simulación; no mueve dinero real ni se conecta con bancos.
- Los datos están en memoria y se reinician al cerrar.
- Las notificaciones son internas; no se envían por WhatsApp, Gmail o SMS.
- El beneficio de bono de $50 se acredita; los descuentos de comisiones no se calculan porque no se definen tarifas.
