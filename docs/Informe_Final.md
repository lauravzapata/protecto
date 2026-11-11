# Informe final — Monedero Plus

## 1. Introducción
Monedero Plus es un simulador de monedero virtual que permite administrar clientes y sus monederos, realizar movimientos de dinero, consultar historiales, acumular y canjear puntos, programar movimientos y generar reportes.

## 2. Pensamiento computacional
### Abstracción
Se representaron los elementos principales mediante las clases Cliente, Monedero, Transaccion, MovimientoPuntos, Beneficio y TransaccionProgramada. Se excluyeron conexiones bancarias reales porque no son necesarias para la simulación.

### Descomposición
El problema se dividió en clientes, monederos, movimientos, puntos, programación, reportes y alertas, e interfaz gráfica.

### Reconocimiento de patrones
Depósitos, retiros y transferencias comparten código, fecha, monto, cliente y estado. Por eso se creó la clase abstracta Transaccion. Los clientes pueden tener varios monederos y movimientos, almacenados en listas.

### Codificación
Se implementó en Java con clases, listas, métodos, condicionales, ciclos, expresiones lambda, Stream API, excepciones personalizadas y JavaFX.

## 3. Programación orientada a objetos
- **Abstracción:** Transaccion es abstracta y representa un movimiento general.
- **Encapsulamiento:** los atributos se declaran privados y se accede mediante métodos.
- **Herencia:** Deposito, Retiro y Transferencia heredan de Transaccion.
- **Polimorfismo:** las clases hijas implementan getTipo() y getDetalle() de forma diferente.
- **Interfaces:** Cliente implementa INotificable.

## 4. Estructuras y programación declarativa
Se utilizan listas para clientes, monederos, transacciones y puntos. Optional representa búsquedas sin resultado. Stream API permite filtrar, buscar, sumar, ordenar y agrupar datos mediante operaciones como filter, findFirst, mapToDouble, sorted y groupingBy.

## 5. Excepciones y validaciones
Se implementaron:
1. DatosInvalidosException: datos vacíos, correo inválido, documento repetido o monto no positivo.
2. ClienteNoEncontradoException: documento que no corresponde a un cliente.
3. SaldoInsuficienteException: saldo insuficiente para retirar, transferir o revertir.

## 6. Puntos y rangos
Se asigna 1 punto por cada 100 unidades depositadas, 2 por cada 100 retiradas y 3 por cada 100 transferidas. Los rangos son Bronce (0–500), Plata (501–1000), Oro (1001–5000) y Platino (más de 5000). Los movimientos de puntos se registran y se pueden anular al revertir una transacción.

## 7. Transacciones programadas
Las transacciones pendientes se filtran y ordenan por fecha usando sorted. El ordenamiento tiene complejidad O(p log p), donde p es el número de operaciones pendientes. Se admiten depósitos y transferencias con frecuencia única, semanal o mensual.

## 8. Detección de movimientos atípicos
Se calcula el promedio y la desviación estándar poblacional de los movimientos anteriores. Una operación se marca cuando su monto supera promedio + 2 × desviación estándar. El análisis realiza recorridos lineales, por lo que su complejidad temporal es O(n), donde n es el número de movimientos históricos.

## 9. Interfaz gráfica
La aplicación utiliza JavaFX con pestañas para clientes, monederos, movimientos, puntos, transacciones programadas, reportes y alertas.

## 10. Pruebas
La clase SistemaMonederoTest contiene pruebas para depósitos, retiros con saldo insuficiente, transferencias, reversión, puntos, programación, canje de beneficios y detección de movimientos atípicos. Se ejecutan con mvn test.

## 11. Alcance y limitaciones
Es una simulación académica, no mueve dinero real ni se conecta con bancos. Los datos se mantienen en memoria y se reinician al cerrar la aplicación. Las notificaciones se muestran dentro del sistema y no se envían por WhatsApp, correo o SMS. Los beneficios de descuento y retiro sin cargos se registran como canjeados, pero no se aplican comisiones monetarias porque el enunciado no define sus tarifas. El bono de saldo sí se acredita al monedero seleccionado.
