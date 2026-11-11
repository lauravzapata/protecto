# MonederoPlus

Proyecto final de Programación 1: simulador de un monedero virtual con sistema de puntos.

## Objetivo
Desarrollar una aplicación de escritorio para administrar clientes y monederos, realizar operaciones financieras simuladas y consultar puntos, rangos, beneficios e informes.

## Funcionalidades planteadas
- Registro y consulta de clientes.
- Múltiples monederos por cliente.
- Depósitos, retiros y transferencias.
- Historial y reversión de transacciones.
- Acumulación y canje de puntos.
- Rangos Bronce, Plata, Oro y Platino.
- Transacciones programadas.
- Reportes de gastos y detección de movimientos atípicos.
- Interfaz gráfica con JavaFX.

## Tecnologías y conceptos
- Java 17
- Maven
- JavaFX
- Programación orientada a objetos
- Expresiones lambda y Stream API
- Excepciones personalizadas
- Pruebas con JUnit 5

## Ejecutar
Con JDK 17 y Maven instalados:

```bash
mvn clean javafx:run
```

Ejecutar pruebas:

```bash
mvn test
```

> Proyecto académico. No procesa dinero real ni se conecta a entidades financieras.

## Estado
El repositorio contiene el código fuente, las pruebas automatizadas y la documentación técnica y de sustentación. Los datos de la aplicación se mantienen en memoria durante la ejecución.
