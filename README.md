# MonederoPlus

Proyecto final de Programación 1: simulador de un monedero virtual con sistema de puntos.

## Objetivo
Desarrollar una aplicación de escritorio para administrar clientes y monederos, realizar operaciones financieras simuladas y consultar puntos, rangos, beneficios e informes.

## Funcionalidades
- Registro y consulta de clientes.
- Múltiples monederos por cliente.
- Depósitos, retiros y transferencias.
- Historial y reversión de transacciones.
- Acumulación y canje de puntos.
- Rangos Bronce, Plata, Oro y Platino.
- Transacciones programadas.
- Reportes de gastos y detección de movimientos atípicos.
- Interfaz gráfica JavaFX diseñada con FXML.

## Organización del proyecto
- `model`: clases y reglas del negocio.
- `controller`: conexión con las operaciones del modelo.
- `viewcontroller`: eventos y componentes de la interfaz.
- `resources/view`: diseño de pantallas en archivos FXML.
- `app/Main.java`: inicio de la aplicación.

## Tecnologías
- Java 17
- Maven
- JavaFX Controls y FXML
- Programación orientada a objetos
- JUnit 5

## Ejecutar
Con JDK 17 y Maven instalados:

```bash
mvn clean javafx:run
```

Ejecutar pruebas:

```bash
mvn test
```

> Proyecto académico. No procesa dinero real ni se conecta a entidades financieras. Los datos se mantienen en memoria durante la ejecución.
