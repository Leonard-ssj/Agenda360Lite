## Problema detectado
- El módulo `Agenda360Lite:app` falla al resolver el plugin `org.jetbrains.kotlin.kapt` por conflicto de versión: el plugin ya está en el classpath (cargado por Kotlin) y Gradle no puede comprobar compatibilidad con la versión del alias.

## Plan de corrección (Android)
1. Eliminar el alias del plugin kapt:
- Quitar `alias(libs.plugins.kotlin.kapt)` del bloque `plugins` en `app/build.gradle.kts`.
- Quitar la entrada `[plugins] kotlin-kapt` de `gradle/libs.versions.toml`.

2. Aplicar kapt con la misma versión de Kotlin cargada:
- Añadir `plugins { kotlin("kapt") }` en `app/build.gradle.kts` junto a los demás plugins.
- Mantener `kapt(libs.dagger.hilt.compiler)` y `kapt(libs.room.compiler)` en `dependencies`.

3. Baseline de Hilt (evitar errores posteriores):
- Crear `Agenda360LiteApp` con `@HiltAndroidApp` y declararlo en `AndroidManifest.xml` (`application android:name`).
- Mantener plugin `com.google.dagger.hilt.android` y dependencia `hilt-android`.

4. Verificar compilación:
- Sincronizar Gradle y compilar el módulo app.

## Backend: consolidar JWT/ENV
1. `.env` real:
- Crear `backend-ktor/.env` a partir de `.env.example` (con `JWT_SECRET`, `JWT_ISSUER=agenda360`, `JWT_AUDIENCE=agenda360-audience`, credenciales DB).
- Confirmar que `Security.kt` lee de `.env`.

2. Prueba de login:
- Levantar backend con `gradlew run`.
- Postman `POST /api/v1/auth/login` con `{"email":"test@demo.com","password":"pass"}` (ya validado 200).

## Pruebas integradas
1. Ejecutar backend (`http://localhost:8080`).
2. Ejecutar app en emulador; `RetrofitClient` usa `http://10.0.2.2:8080`.
3. Login con usuario demo y verificar navegación a `dashboard` y token guardado en `DataStore`.

## Entregables tras corrección
- App compila y corre sin conflictos de kapt.
- Backend operando con JWT desde `.env`.
- Login end-to-end funcional.
