# Sistema de Gestión de Equipos Mineros - Backend

API REST desarrollada en Java 21 y Spring Boot 3 bajo arquitectura Domain-Driven Design (DDD) y CQRS para la gestión operativa y mantenimiento predictivo de maquinaria minera.

---

## 1. ¿Qué hace?

- **Maquinaria**: Registro de flota, seguimiento de horómetro acumulado, control de estados (`ACTIVO`, `BLOQUEADO`) y bloqueo automático al alcanzar el umbral de mantenimiento.
- **Operadores y Certificaciones**: Registro de operadores y validación estricta de vigencia de licencias por tipo de equipo antes de permitir asignaciones.
- **Turnos y Asignaciones**: Programación de jornadas (Día/Noche), asignación de operador y equipo con validación concurrente en tiempo real (evita duplicados y rechaza equipos bloqueados o sin certificación).
- **Mantenimiento Preventivo**: Registro de intervenciones técnicas con reinicio automático de horómetro a 0 y desbloqueo inmediato del equipo.
- **Proyección Preventiva a 7 Días**: Cálculo predictivo para anticipar la fecha y turno exacto en que cada maquinaria alcanzará su umbral de horas.
- **Seeder de Pruebas**: Endpoint `POST /api/v1/test-data/reset` para limpiar la base de datos y cargar datos iniciales de prueba para casos de borde.

---

## 2. Cómo levantarlo en local

### Prerrequisitos
- **Java 21 (JDK)**
- **MySQL 8.x** (o Docker)

### Paso 1: Base de datos
Crear la base de datos en MySQL:
```sql
CREATE DATABASE codeaunidb;
```

*(Opcional usando Docker Compose):*
```bash
docker compose up -d
```

Verificar credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/codeaunidb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_password
```

### Paso 2: Ejecutar la aplicación
Desde el directorio del proyecto (`desafio_fullstack`):

- **Windows:**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- **Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```

La API quedará escuchando en `http://localhost:8080`.

### Paso 3: Documentación Swagger / OpenAPI
Accede desde el navegador a:
- `http://localhost:8080/swagger-ui/index.html`

---

## 3. Cómo se desplegó

El backend está desplegado en **Render** y conectado a una base de datos en **Aiven.io**:

1. **Base de Datos en Aiven (Aiven.io)**:
   - Se aprovisionó un servicio gestionado de **MySQL** en Aiven.
   - Se configuraron los parámetros de conexión seguros (Host, Puerto, Base de datos, Usuario y Password con SSL obligatorio).

2. **Dockerización**:
   - Se utiliza el `dockerfile` multi-stage con imagen base `eclipse-temurin:21/jdk/alpine` para compilar el `.jar` omitiendo tests en el build y ejecutarlo con un usuario sin privilegios.
   - La aplicación toma dinámicamente el puerto mediante la variable de entorno `PORT`.

3. **Web Service en Render**:
   - Servicio tipo **Web Service** conectado al repositorio de GitHub.
   - Entorno configurado en **Docker** (detecta automáticamente el `dockerfile`).
   - Variables de entorno configuradas en Render:
     - `PORT=8080`
     - `SPRING_DATASOURCE_URL=jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/<AIVEN_DB>?sslMode=REQUIRED&serverTimezone=UTC`
     - `SPRING_DATASOURCE_USERNAME=<AIVEN_USER>`
     - `SPRING_DATASOURCE_PASSWORD=<AIVEN_PASSWORD>`
