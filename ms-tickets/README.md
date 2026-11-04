MS-Tickets
Integrantes del equipo
Diego Jimenez

Kevin Gonzalez

Jose Sanchez

Descripción
Microservicio encargado de la gestión de entradas, venta de tickets y control de aforo para el festival. Permite administrar los tipos de tickets disponibles (General, VIP, Preventas), procesar las compras de los asistentes y validar las transacciones. Utiliza comunicación mediante Feign Client para verificar con MS-Eventos la disponibilidad de los sectores del festival y con MS-Usuarios para asociar la compra al cliente correcto.

Tecnologías
Spring Boot 3.4.5

MySQL

OpenFeign (Feign Client)

Spring Security & JWT

Spring Data JPA & Hibernate

Lombok

Puerto
MS-Tickets: 8803 (Puerto secuencial estándar para este módulo)

Endpoints
Gestión de Tipos de Tickets (TicketTypeController)
POST /api/v1/tickets/types - Crear una nueva categoría de entrada (ej: VIP Preventa 1) asignando precio y stock máximo.

GET /api/v1/tickets/types/evento/{eventoId} - Consultar los tipos de tickets y precios disponibles para un evento específico.

PUT /api/v1/tickets/types/{id} - Modificar precio o stock de una categoría de entrada.

Procesamiento de Compras (VentaController)
POST /api/v1/tickets/comprar - Registrar la compra de entradas (valida stock libre, asocia el usuarioId y genera los códigos únicos).

GET /api/v1/tickets/usuario/{usuarioId} - Listar todos los tickets adquiridos por un usuario en particular.

GET /api/v1/tickets/validar/{codigoTicket} - Endpoint utilizado por el staff para validar la autenticidad de la entrada en el acceso al festival.

DELETE /api/v1/tickets/cancelar/{id} - Anular una compra o ticket por devolución/problemas con el pago.

Requisitos
Java 17 (Eclipse Adoptium OpenJDK o similar)

Base de datos MySQL activa en ambiente local o remoto

MS-Usuarios corriendo en el puerto 8801 (para validar el comprador)

MS-Eventos corriendo en el puerto 8802 (para validar fechas y recintos del festival)

Pasos para ejecutar
Asegúrate de tener levantada la base de datos MySQL correspondiente para el proyecto del festival.

Asegúrate de que los microservicios MS-Usuarios (8801) y MS-Eventos (8802) estén activos en tu entorno.

Abre una terminal en la raíz de la carpeta del proyecto (ms-tickets).

Ejecuta el comando de construcción y arranque de Maven:

Bash
./mvnw spring-boot:run