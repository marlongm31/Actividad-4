# Arquitectura de BarberTurn

## 1. Vision general

BarberTurn usa **Arquitectura Hexagonal (Ports and Adapters)** en el backend. La regla central es que el dominio y los casos de uso no conocen Spring, HTTP, JPA ni JWT. Toda tecnologia externa entra o sale mediante puertos.

```text
                    +---------------------------+
   HTTP / React --->|  Adaptadores de entrada   |
                    |  web, seguridad JWT       |
                    +-------------+-------------+
                                  |
                                  v
                    +---------------------------+
                    |      Aplicacion           |
                    |  casos de uso + puertos   |
                    +-------------+-------------+
                                  |
                                  v
                    +---------------------------+
                    |        Dominio            |
                    | modelo + reglas + errores |
                    +-------------+-------------+
                                  ^
                                  |
                    +-------------+-------------+
                    |  Adaptadores de salida    |
                    |  JPA, BCrypt, JWT         |
                    +-------------+-------------+
```

Flujo de dependencias: `adapter -> application -> domain`. El dominio no importa nada de las capas externas.

## 2. Capas y responsabilidades

### Dominio

Paquete `com.barberturn.domain`.

| Elemento | Responsabilidad |
| --- | --- |
| `Service` | Servicio ofrecido, precio, duracion y estado activo. Valida datos propios. |
| `Appointment` | Cita con cliente, fecha, hora, servicio y estado. Controla transiciones. |
| `AppointmentStatus` | Estados validos y transiciones permitidas. |
| `BusinessHours` | Horario de atencion por dia. Valida apertura y cierre. |
| `AdminUser` | Cuenta administrativa. |
| `TimeSlot` | Bloque disponible con inicio y fin. |
| `AvailabilityCalculator` | Genera slots y valida solapamientos, horario y fechas pasadas. |
| Excepciones | `DomainException`, `BusinessRuleException`, `ConflictException`, `NotFoundException`, `InvalidCredentialsException`. |

El dominio usa `java.time` y `BigDecimal`, no tipos de persistencia ni anotaciones de framework.

### Aplicacion

Paquete `com.barberturn.application`.

- **Puertos de entrada:** interfaces que exponen los casos de uso (`ServiceCatalogUseCase`, `AvailabilityUseCase`, `BookAppointmentUseCase`, `AppointmentManagementUseCase`, `BusinessHoursUseCase`, `AuthUseCase`).
- **Puertos de salida:** contratos que la aplicacion necesita (`ServiceRepositoryPort`, `AppointmentRepositoryPort`, `BusinessHoursRepositoryPort`, `AdminAccountPort`, `PasswordHasherPort`, `TokenProviderPort`).
- **Casos de uso:** implementaciones con inyeccion por constructor. Orquestan dominio y puertos.
- **DTOs internos:** `BookAppointmentCommand`, `ServiceCommand`, `BusinessHoursCommand`, `LoginCommand`, `AuthResult`, `DashboardStats`.

Los DTOs de aplicacion no dependen de los DTOs HTTP. Los controladores traducen entre ambos.

### Adaptadores de entrada

- `adapter/in/web`: controladores REST delgados, validacion Jakarta y mappers de respuesta.
- `adapter/in/security`: `SecurityConfig` y `JwtAuthenticationFilter`.
- `GlobalExceptionHandler`: traduce excepciones de dominio a respuestas HTTP consistentes.

### Adaptadores de salida

- `adapter/out/persistence`: entidades JPA, repositorios Spring Data, mappers y adaptadores que implementan los puertos.
- `adapter/out/security`: `BCryptPasswordHasherAdapter` y `JwtTokenAdapter`.

### Infraestructura

- `ApplicationConfig`: registra casos de uso como beans y define el `Clock`.
- `JwtProperties`, `AdminProperties`, `CorsProperties`: configuracion tipada.
- `AdminAccountSeeder`: crea la cuenta administrativa inicial de forma idempotente.

## 3. Flujos principales

### Reserva de cita

1. `PublicController` valida el request HTTP.
2. `BookAppointmentService` busca el servicio y el horario del dia.
3. `AvailabilityCalculator` valida fecha, horario y solapamientos.
4. La cita se crea en estado `PENDING`.
5. `AppointmentPersistenceAdapter` guarda mediante JPA.
6. El controlador devuelve `201 Created` con la cita.

### Disponibilidad

1. `AvailabilityService` obtiene servicio y horario.
2. `AvailabilityCalculator` genera bloques de 15 minutos.
3. Descarta bloques pasados, fuera de horario o que se solapan con citas activas.
4. Devuelve solo los slots donde cabe la duracion completa del servicio.

### Autenticacion

1. `AuthController` recibe correo y contrasena.
2. `AuthService` normaliza el correo y busca la cuenta.
3. `PasswordHasherPort` compara con BCrypt.
4. `TokenProviderPort` emite un JWT.
5. El frontend lo guarda y lo envia como `Authorization: Bearer <token>`.

### Cambio de estado

1. `AdminAppointmentController` recibe el nuevo estado.
2. `AppointmentManagementService` carga la cita.
3. `Appointment.withStatus` delega en `AppointmentStatus`.
4. Si la transicion no es valida, lanza `DomainException` y se responde `400`.

## 4. Aplicacion de SOLID

### S - Single Responsibility

- Cada caso de uso tiene una responsabilidad: reservar, consultar disponibilidad, autenticar, gestionar citas, administrar servicios u horarios.
- `AvailabilityCalculator` solo calcula disponibilidad; no accede a base de datos.
- Los controladores solo adaptan HTTP.
- Los mappers convierten entre modelos y DTOs.

Ejemplos: `application/usecase/BookAppointmentService.java`, `domain/service/AvailabilityCalculator.java`, `adapter/in/web/PublicController.java`.

### O - Open/Closed

Los puertos permiten nuevas implementaciones sin modificar el dominio. Por ejemplo, se puede crear un adaptador de persistencia en memoria o PostgreSQL implementando `AppointmentRepositoryPort` sin tocar `BookAppointmentService`.

### L - Liskov Substitution

Los adaptadores respetan el contrato de sus puertos. `AppointmentPersistenceAdapter`, `BCryptPasswordHasherAdapter` y `JwtTokenAdapter` pueden sustituirse por otras implementaciones sin alterar los casos de uso.

### I - Interface Segregation

Los puertos son pequenos y especificos. `PasswordHasherPort` solo expone `hash` y `matches`; `TokenProviderPort` solo crea y valida tokens. No existe una interfaz general de persistencia con metodos innecesarios.

### D - Dependency Inversion

- El dominio no depende de frameworks.
- Los casos de uso dependen de interfaces, no de clases JPA.
- `ApplicationConfig` inyecta las implementaciones concretas.
- Las entidades JPA son independientes de los modelos de dominio.

## 5. Clean Code y Refactoring

Decisiones aplicadas:

- **Lenguaje ubicuo:** nombres de negocio en espanol en la UI y terminos tecnicos consistentes en el codigo (`Appointment`, `Service`, `BusinessHours`).
- **Records** para modelos y DTOs inmutables.
- **Funciones pequenas** con una sola intencion.
- **Constantes nombradas** como `SLOT_INTERVAL_MINUTES`.
- **Sin logica de negocio en controladores.**
- **Separacion modelo de dominio / entidad JPA / DTO HTTP.**
- **Inyeccion por constructor** y campos `final`.
- **Manejo centralizado de errores** con `GlobalExceptionHandler`.
- **Normalizacion de entradas** antes de persistir.
- **Flyway** como fuente de verdad del esquema; `ddl-auto: validate`.
- **Pruebas unitarias** del dominio sin levantar Spring.

Refactorings representativos:

1. La generacion de slots y validacion de solapamientos se extrajo del controlador a `AvailabilityCalculator`.
2. Las entidades JPA se separaron del modelo de dominio para evitar acoplamiento.
3. Los DTOs web se separaron de los comandos de aplicacion.
4. La seguridad se aislo en adaptadores detras de `PasswordHasherPort` y `TokenProviderPort`.
5. La creacion del administrador se movio a un seeder idempotente.
6. La configuracion de CORS y JWT se movio a propiedades tipadas.

## 6. Estrategia de pruebas

| Nivel | Herramienta | Que cubre |
| --- | --- | --- |
| Dominio | JUnit 5 + AssertJ | Slots, solapamientos, reglas de horario, transiciones de estado |
| Casos de uso | JUnit 5 + Mockito | Reserva, conflictos, autenticacion, gestion y estadisticas |
| Utilidades frontend | Vitest | Fechas, moneda, duracion y etiquetas de estado |

Las pruebas de casos de uso usan mocks de los puertos; no requieren base de datos ni contexto de Spring. Esto mantiene las pruebas rapidas y enfocadas en comportamiento.

## 7. Tradeoffs y limites

- **Un solo barbero:** simplifica el modelo y la agenda; no hay recursos ni empleados.
- **Slots fijos de 15 minutos:** reduce complejidad, pero puede dejar espacios no aprovechables para servicios con duraciones irregulares.
- **JWT sin revocacion:** apropiado para el alcance academico; en produccion se recomienda refresh token, rotacion y lista de revocacion.
- **Solapamiento validado en aplicacion:** suficiente para un unico administrador; con alta concurrencia se necesita bloqueo pesimista o restriccion en base de datos.
- **Un rol administrativo:** no hay permisos granulares.
- **Zona horaria fija:** el sistema esta pensado para `America/Bogota`.

## 8. Posibles mejoras

- Notificaciones por correo o WhatsApp.
- Recordatorios y cancelacion por parte del cliente.
- Multiples barberos y recursos.
- Bloqueo pesimista o indice unico para reservas concurrentes.
- Panel de reportes por rango de fechas.
- Refresh tokens y cierre de sesion del lado servidor.
- Pruebas de integracion con Testcontainers.
- Despliegue con Docker Compose y pipeline CI.
