# BarberTurn

Aplicacion web full stack para la gestion de citas de una peluqueria con un solo barbero. Permite a los clientes consultar servicios, ver horarios disponibles y reservar una cita; y al administrador iniciar sesion para gestionar agenda, servicios, horario de atencion y estadisticas del dia.

Proyecto desarrollado para la **Actividad 4** de **Topicos Avanzados de Software**, aplicando Arquitectura Hexagonal, principios SOLID, Clean Code y Refactoring.

> **Docente:** la guia de evaluacion paso a paso esta en [GUIA_DOCENTE.md](GUIA_DOCENTE.md).

## Funcionalidades

### Zona publica

- Catalogo de servicios activos con precio y duracion.
- Consulta de disponibilidad por fecha y servicio.
- Reserva de citas con nombre, telefono, correo y notas opcionales.
- Confirmacion inmediata con codigo de cita y resumen.
- Diseno responsive con identidad visual de barberia.

### Zona administrativa

- Inicio de sesion con JWT.
- Panel con citas del dia, estados e ingresos estimados.
- Listado y filtrado de citas por fecha y estado.
- Cambio de estado: pendiente, confirmada, completada o cancelada.
- Creacion, edicion y desactivacion de servicios.
- Configuracion del horario de atencion por dia de la semana.

## Tecnologias

| Capa | Tecnologias |
| --- | --- |
| Backend | Java 21, Spring Boot 3.4.5, Spring Web, Spring Data JPA, Spring Security |
| Persistencia | MySQL 8, Flyway, Hibernate |
| Seguridad | JWT (jjwt 0.12.6), BCrypt |
| Pruebas backend | JUnit 5, Mockito, AssertJ |
| Frontend | React 19, TypeScript, Vite 6, React Router 7, Axios, Lucide React |
| Pruebas frontend | Vitest |
| Build | Maven, npm |

## Arquitectura

El backend sigue Arquitectura Hexagonal: el dominio y los casos de uso no dependen de Spring ni de la base de datos; los controladores, la persistencia y la seguridad se conectan mediante puertos y adaptadores.

```text
src/main/java/com/barberturn
├── domain/               Modelo, reglas y excepciones de negocio
├── application/          Casos de uso, puertos y DTOs de aplicacion
├── adapter/
│   ├── in/web            Controladores REST y mappers de entrada
│   ├── in/security       Filtro JWT y configuracion de seguridad
│   └── out/              Adaptadores JPA y seguridad
└── infrastructure/       Configuracion, propiedades y seeder
```

La explicacion detallada, los flujos y la aplicacion de SOLID estan en [ARCHITECTURE.md](ARCHITECTURE.md).

## Estructura del repositorio

```text
.
├── backend/                      API REST Spring Boot
│   ├── src/main/java/            Codigo de produccion
│   ├── src/main/resources/       Configuracion y migraciones Flyway
│   └── src/test/java/            Pruebas unitarias
├── frontend/                     Aplicacion React + TypeScript
│   ├── src/                      Paginas, componentes, contextos y utilidades
│   └── .env.example              Variables del frontend
├── database/                     Script de creacion de base y usuario
├── scripts/                      Arranque y parada de MySQL local
├── ARCHITECTURE.md               Documento de arquitectura y decisiones
├── GUIA_DOCENTE.md               Guia de evaluacion paso a paso
└── README.md
```

## Requisitos

- Java 21
- Maven 3.9+
- Node.js 20+ y npm
- MySQL 8 en `localhost:3306` (o una instancia propia configurada con `DB_URL`)

## Puesta en marcha

### 1. Base de datos

El backend usa por defecto:

```text
jdbc:mysql://localhost:3306/barberturn
usuario: barberturn
contrasena: barberturn
```

Ejecuta el script para crear la base y el usuario:

```bash
mysql -u root -p < database/01_create_database.sql
```

Si usas otro puerto, host o credenciales, define `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` antes de arrancar el backend.

Si no puedes iniciar el servicio MySQL de Windows, este repositorio incluye una instancia local aislada en el puerto 3306:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-local-mysql.ps1
```

El script crea el directorio de datos en `.local/mysql-data`, inicializa MySQL si es necesario, arranca la instancia, crea la base `barberturn` y el usuario de aplicacion. Para detenerla:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/stop-local-mysql.ps1
```

Si el puerto 3306 ya esta ocupado, arranca la instancia local en otro puerto y configura el backend:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-local-mysql.ps1 -Port 3307
$env:DB_URL="jdbc:mysql://localhost:3307/barberturn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota"
```

### 2. Backend

```powershell
cd backend
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Flyway crea las tablas y carga los servicios y horarios iniciales en el primer arranque.

### 3. Frontend

```powershell
cd frontend
npm install
npm run dev
```

Abrir `http://localhost:5173`. Vite redirige las peticiones `/api` al backend en `http://localhost:8080`.

### 4. Acceso administrativo

- URL: `http://localhost:5173/admin/login`
- Correo: `admin@barberturn.com`
- Contrasena: `BarberTurn2026!`

Estas credenciales son de desarrollo y se crean automaticamente si no existe la cuenta. Se pueden cambiar con `ADMIN_EMAIL`, `ADMIN_PASSWORD` y `ADMIN_NAME` antes del primer arranque.

## Configuracion

Variables de entorno soportadas por el backend:

| Variable | Valor por defecto | Descripcion |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | Puerto de la API |
| `DB_URL` | `jdbc:mysql://localhost:3306/barberturn?...` | URL JDBC |
| `DB_USERNAME` | `barberturn` | Usuario de base de datos |
| `DB_PASSWORD` | `barberturn` | Contrasena de base de datos |
| `JWT_SECRET` | Clave de desarrollo | Secreto para firmar JWT (minimo 32 caracteres) |
| `JWT_EXPIRATION_MINUTES` | `480` | Vigencia del token en minutos |
| `ADMIN_EMAIL` | `admin@barberturn.com` | Correo del administrador inicial |
| `ADMIN_PASSWORD` | `BarberTurn2026!` | Contrasena del administrador inicial |
| `ADMIN_NAME` | `Administrador BarberTurn` | Nombre visible del administrador |
| `CORS_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | Origenes permitidos |

El frontend solo requiere `VITE_API_URL`, por defecto `/api` para usar el proxy de Vite.

En produccion se debe cambiar `JWT_SECRET`, la contrasena del administrador y las credenciales de base de datos.

## API REST

### Publica

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/public/services` | Servicios activos |
| `GET` | `/api/public/availability?date=YYYY-MM-DD&serviceId=1` | Horarios disponibles |
| `POST` | `/api/public/appointments` | Crear una cita |

### Autenticacion

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Iniciar sesion y obtener JWT |

### Administracion (Bearer token)

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| `GET` | `/api/admin/appointments?date=&status=` | Listar y filtrar citas |
| `GET` | `/api/admin/appointments/{id}` | Detalle de cita |
| `PATCH` | `/api/admin/appointments/{id}/status` | Cambiar estado |
| `GET` | `/api/admin/dashboard?date=` | Estadisticas del dia |
| `GET` | `/api/admin/services` | Listar todos los servicios |
| `POST` | `/api/admin/services` | Crear servicio |
| `PUT` | `/api/admin/services/{id}` | Actualizar servicio |
| `DELETE` | `/api/admin/services/{id}` | Desactivar servicio |
| `GET` | `/api/admin/business-hours` | Consultar horario semanal |
| `PUT` | `/api/admin/business-hours` | Actualizar horario semanal |

### Ejemplo de reserva

```bash
curl -X POST http://localhost:8080/api/public/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": 1,
    "date": "2026-11-16",
    "startTime": "10:00",
    "customerName": "Juan Perez",
    "customerPhone": "3001234567",
    "customerEmail": "juan@example.com",
    "notes": "Primera visita"
  }'
```

### Formato de error

```json
{
  "status": 409,
  "error": "Conflicto de horario",
  "message": "El horario seleccionado ya fue reservado. Elige otro horario.",
  "fieldErrors": null
}
```

Codigos usados: `400` validacion o regla de negocio, `401` credenciales invalidas o token ausente, `404` recurso inexistente, `409` solapamiento de horario.

## Reglas de negocio

- Un solo barbero; no se solapan citas.
- Slots cada 15 minutos dentro del horario de atencion.
- La duracion del servicio determina el bloque ocupado.
- No se reservan fechas ni horas pasadas.
- No se reservan citas en dias inactivos ni fuera del horario configurado.
- Estados: `PENDING -> CONFIRMED | CANCELLED`, `CONFIRMED -> COMPLETED | CANCELLED`.
- `COMPLETED` y `CANCELLED` son estados finales.
- Los ingresos estimados excluyen citas canceladas.
- La validacion de disponibilidad ocurre en el backend, no solo en la interfaz.

## Pruebas

Backend:

```powershell
cd backend
mvn test
```

Cobertura actual: 32 pruebas unitarias de dominio y casos de uso.

Frontend:

```powershell
cd frontend
npm run test
```

Cobertura actual: 12 pruebas de utilidades de fechas, moneda y estados.

Compilacion de produccion:

```powershell
cd backend
mvn clean package

cd ../frontend
npm run build
```

## Decisiones y alcance

- JWT stateless con expiracion de 8 horas; no se implementa refresh token ni lista de revocacion.
- Un unico rol administrativo.
- La deteccion de solapamientos se hace en la capa de aplicacion; para alta concurrencia se recomienda una restriccion o bloqueo a nivel de base de datos.
- Zona horaria `America/Bogota` y formato de moneda COP.
- Flyway es la fuente de verdad del esquema; Hibernate solo valida.

Mas detalles en [ARCHITECTURE.md](ARCHITECTURE.md).

## Solucion de problemas

**`Error de sistema 5. Acceso denegado` al iniciar MySQL80**

El servicio requiere permisos de administrador. Usa la instancia local incluida:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-local-mysql.ps1
```

**`Communications link failure`**

Verifica que MySQL escuche en el puerto configurado:

```powershell
Test-NetConnection localhost -Port 3306
```

**Cambiar de puerto**

Define `DB_URL` antes de arrancar el backend:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3307/barberturn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota"
mvn spring-boot:run
```

**Reiniciar datos de demostracion**

Detiene el backend, elimina el contenido de las tablas `appointments` y reinicia; Flyway no vuelve a ejecutar migraciones ya aplicadas. Para recrear todo desde cero, elimina la base y vuelve a crearla con el script de `database/`.

## Repositorio Git y GitHub Desktop

El proyecto incluye `.gitignore` para excluir dependencias, builds, variables locales y datos de MySQL. Para publicarlo:

1. Abrir GitHub Desktop y elegir **Add local repository**.
2. Seleccionar la carpeta raiz del proyecto.
3. Crear el repositorio local si GitHub Desktop lo solicita.
4. Revisar los archivos, hacer el primer commit y publicarlo en GitHub.

No se suben credenciales reales ni el directorio `.local/`.
