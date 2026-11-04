# MS-Logística

## Integrantes del equipo
- Diego Jimenez
- Kevin Gonzales
- Jose Sanchez


## Descripción
Microservicio encargado de la gestión logística del festival: escenarios, zonas y asignación de staff. Valida con MS-Eventos que el evento esté publicado y con MS-Usuarios que el staff tenga el rol correcto.

## Tecnologías
- Spring Boot 3.3.11
- MySQL (AWS)
- Feign Client
- Eureka Client

## Puerto
- MS-Logística: 8805

## Endpoints
- POST /api/v1/escenarios
- GET /api/v1/escenarios/evento/{eventoId}
- DELETE /api/v1/escenarios/{id}
- POST /api/v1/zonas
- GET /api/v1/zonas/evento/{eventoId}
- GET /api/v1/zonas/evento/{eventoId}/sinstaff
- DELETE /api/v1/zonas/{id}
- POST /api/v1/asignaciones/{usuarioId}
- GET /api/v1/asignaciones/staffZona/{zonaId}
- GET /api/v1/asignaciones/staffEvento/{eventoId}
- DELETE /api/v1/asignaciones/{id}

## Requisitos
- Java 17
- Eureka Server corriendo en puerto 8761
- MS-Usuarios corriendo en puerto 8801
- MS-Eventos corriendo en puerto 8802
- MS-Notificaciones corriendo en puerto 8806

## Pasos para ejecutar
1. Iniciar Eureka Server
2. Iniciar MS-Usuarios (8801)
3. Iniciar MS-Eventos (8802)
4. Iniciar MS-Notificaciones (8806)
5. Ejecutar: `./mvnw spring-boot:run`