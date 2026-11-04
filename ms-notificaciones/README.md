# MS-Notificaciones

Microservicio reactivo encargado de registrar notificaciones del sistema del festival. Recibe avisos de otros microservicios y los persiste en base de datos.

## Tecnologías
- Spring Boot 3.3.11
- MySQL
- Eureka Client

## Puerto
- MS-Notificaciones: 8806

## Endpoints principales
- POST /api/v1/notificaciones
- GET /api/v1/notificaciones/usuario/{usuarioId}
- GET /api/v1/notificaciones/tipo/{tipo}
- DELETE /api/v1/notificaciones/{id}

## Tipos de notificación
- COMPRA_TICKET
- CAMBIO_HORARIO
- CAMBIO_ZONA

## Requisitos
- Java 17
- MySQL corriendo en AWS (3.82.123.219:3306)
- Eureka Server corriendo en puerto 8761

## Cómo ejecutar
1. Iniciar Eureka Server
2. Iniciar MS-Notificaciones: `./mvnw spring-boot:run`