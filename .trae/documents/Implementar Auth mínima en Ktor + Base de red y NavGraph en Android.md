## Backend (Ktor) – Fase 1
1. Crear soporte .env seguro:
- Añadir loader para `.env` (dotenv-kotlin o lector simple) y leer `JWT_SECRET`, `JWT_ISSUER`, `JWT_AUDIENCE`, `DB_URL`, `DB_USER`, `DB_PASS`.
- Agregar `/.env` a `.gitignore` y crear `/.env.example` sin secretos.

2. Configuración JWT y módulos:
- Mover valores hardcodeados de `Security.kt` a lectura desde `.env`/`application.yaml`.
- Mantener `ContentNegotiation` con `kotlinx.serialization` y `CallLogging`.

3. DTOs y envelope estándar:
- Definir `LoginRequest{email,password}` y `LoginResponse{token,user{id,name,email}}`.
- Crear wrapper de respuesta `{ data, error, message }`.

4. Servicio y repositorio:
- `AuthService.login(email,password)` valida usuario y genera JWT (expiración 30–60 min, `sub=userId`).
- `UserRepository.findByEmail(email)` inicialmente **stub en memoria** para pruebas; luego conectaremos MySQL.
- Hash de contraseña con `bcrypt` (work factor razonable) o simulación temporal para demo.

5. Rutas:
- `POST /api/v1/auth/login` que usa `AuthService` y devuelve envelope.
- Preparar estructura `/api/v1` para futuras rutas.

6. Pruebas y arranque:
- `.
\gradlew run` y Postman: probar `POST /api/v1/auth/login` (200 con token) y errores (401).
- Documentar cómo setear `.env` y variables.

## Android (Agenda360Lite) – Fase 3/4 base
1. Dependencias:
- Navigation Compose, Hilt (kapt), Retrofit + `kotlinx.serialization` converter, OkHttp Logging (solo DEBUG), Room, DataStore.

2. Estructura y NavGraph:
- Paquetes `core/`, `auth/`, `appointments/`, `clients/`, `services/`, `navigation/`.
- NavGraph con rutas: `login`, `dashboard`, `appointments`, `appointmentDetail/{id}`, `appointmentForm`, `profile`.
- Pantallas dummy iniciales.

3. Red y almacenamiento:
- `RetrofitClient` con `baseUrl` `http://10.0.2.2:8080`.
- `JwtInterceptor` leyendo `DataStore` para añadir `Authorization`.
- `UserPreferences` (guardar `jwtToken`, `userName`, `userEmail`).

4. Auth módulo:
- `AuthApi.login`, `AuthRepository`, `LoginUseCase`, `LoginViewModel` con `UiState`.
- `LoginScreen` que llama `onLoginClick` y navega a `dashboard` en éxito.

5. Pruebas:
- Backend levantado en Trae; app en emulador.
- Login desde app y ver token guardado; llamadas posteriores incluirán `Authorization`.

## Entregables en esta iteración
- Backend: `.env`, JWT configurado, endpoint `/api/v1/auth/login` funcional con envelope.
- Android: dependencias añadidas, NavGraph base, `RetrofitClient`/`JwtInterceptor`/`UserPreferences` listos, `AuthApi` y flujo de login conectado.

## Verificación
- Postman: `POST /api/v1/auth/login` → `200` con `data.token`.
- Emulador: login exitoso, navega a `dashboard`, token persistente en `DataStore`.

## Siguientes iteraciones (resumen)
- Conectar MySQL y repositorios reales (Fase 2).
- CRUDs en backend y app (Fase 5).
- Geo y cámara (Fase 6).