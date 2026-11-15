# Guia rapida para el docente

BarberTurn es una aplicacion full stack de agendamiento para una peluqueria. Esta guia permite ejecutar y evaluar el proyecto desde cero sin revisar el codigo previamente.

## 1. Requisitos

- Java 21
- Maven 3.9 o superior
- Node.js 20 o superior con npm
- MySQL 8 en `localhost:3306`
- Navegador moderno

No se requieren servicios externos, cuentas de terceros ni datos de prueba manuales.

## 2. Crear la base de datos

Desde la raiz del repositorio:

```bash
mysql -u root -p < database/01_create_database.sql
```

Esto crea la base `barberturn` y el usuario:

```text
usuario: barberturn
contrasena: barberturn
```

Las tablas y los datos iniciales se crean automaticamente con Flyway al arrancar el backend.

### Alternativa en Windows si no se puede iniciar el servicio MySQL

Si MySQL 8 esta instalado pero el servicio de Windows no arranca por permisos, usar la instancia local incluida:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-local-mysql.ps1
```

El script usa el puerto 3306 por defecto, inicializa los datos en `.local/mysql-data` y crea la base y el usuario. Para detenerla:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/stop-local-mysql.ps1
```

## 3. Arrancar el backend

En una terminal:

```bash
cd backend
mvn spring-boot:run
```

Esperar hasta ver mensajes similares a:

```text
Successfully applied 2 migrations to schema `barberturn`
Tomcat started on port 8080
Started BarberTurnApplication
```

La API queda en `http://localhost:8080`.

## 4. Arrancar el frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

Abrir:

```text
http://localhost:5173
```

Vite redirige las peticiones `/api` al backend en el puerto 8080.

## 5. Acceso administrativo

```text
URL: http://localhost:5173/admin/login
Correo: admin@barberturn.com
Contrasena: BarberTurn2026!
```

La cuenta se crea automaticamente en el primer arranque si no existe.

## 6. Recorrido recomendado de evaluacion

1. **Pagina principal:** revisar el diseno responsive, el catalogo de servicios y la seccion de reserva.
2. **Reservar una cita:** elegir un servicio, una fecha futura y un horario; completar nombre y telefono. Debe mostrarse la confirmacion con el resumen.
3. **Verificar conflicto:** intentar reservar el mismo horario de nuevo. La API debe responder `409` con el mensaje de horario ya reservado.
4. **Panel administrativo:** iniciar sesion, revisar el dashboard y la cita creada.
5. **Gestionar cita:** cambiar el estado a confirmada o completada. Las transiciones invalidas se rechazan con `400`.
6. **Gestionar servicios:** crear o editar un servicio y verificar que aparece en la pagina publica.
7. **Gestionar horarios:** cambiar el horario de atencion y comprobar que la disponibilidad se actualiza.

Datos iniciales cargados por Flyway:

- 5 servicios activos con precios y duraciones.
- Horario de lunes a viernes 09:00-19:00, sabado 09:00-18:00 y domingo inactivo.
- Slots de atencion cada 15 minutos.

## 7. Ejecutar las pruebas

Backend:

```bash
cd backend
mvn test
```

Resultado esperado: **32 pruebas exitosas**.

Frontend:

```bash
cd frontend
npm run test
```

Resultado esperado: **12 pruebas exitosas**.

Compilacion de produccion:

```bash
cd backend
mvn clean package

cd ../frontend
npm run build
```

## 8. Donde revisar la arquitectura

- [README.md](README.md): funcionalidades, API, configuracion y puesta en marcha.
- [ARCHITECTURE.md](ARCHITECTURE.md): Arquitectura Hexagonal, SOLID, Clean Code, Refactoring y decisiones.
- `backend/src/main/java/com/barberturn/domain`: modelo y reglas de negocio sin dependencias de framework.
- `backend/src/main/java/com/barberturn/application`: casos de uso y puertos.
- `backend/src/main/java/com/barberturn/adapter`: adaptadores HTTP, JPA y seguridad.
- `frontend/src`: paginas publicas, panel administrativo, contextos y utilidades.

## 9. Solucion de problemas

**MySQL no responde o `Communications link failure`**

```powershell
Test-NetConnection localhost -Port 3306
```

Si el puerto esta ocupado por otra instancia, arrancar la instancia local en 3307 y definir la URL del backend:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/start-local-mysql.ps1 -Port 3307
$env:DB_URL="jdbc:mysql://localhost:3307/barberturn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota"
```

**Credenciales de base de datos distintas**

Definir antes de arrancar el backend:

```powershell
$env:DB_USERNAME="usuario"
$env:DB_PASSWORD="contrasena"
$env:DB_URL="jdbc:mysql://localhost:3306/barberturn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota"
```

**Puerto 8080 o 5173 ocupado**

Cerrar el proceso anterior o cambiar el puerto del backend con `SERVER_PORT`. Para el frontend, Vite elige el siguiente puerto disponible e imprime la URL en la terminal.
