# Cambios para la Evaluación Parcial 4

Qué se agregó para la entrega del 24 de noviembre, ordenado según la pauta. Los cambios visuales anteriores (antes del video) están en `PENDIENTES.md`.

| Indicador de la pauta | Peso | Estado |
|---|---|---|
| 3.1.1 App completa | 15% | Listo (entregas anteriores) |
| 3.1.2 Microservicios con base de datos | 15% | **Hecho** (23-nov) |
| 3.1.3 App integrada con los microservicios (CRUD) | 15% | **Hecho** (23-nov) |
| 3.1.4 API externa | 15% | **Hecho** (23-nov) |
| 3.2.1 Pruebas unitarias (80%) | 15% | **Hecho** (23-nov): 88 pruebas, 95,6% de líneas |
| 3.3.1 APK firmado | 10% | **Hecho** (23-nov) |
| 3.3.2 GitHub y Trello | 15% | Pendiente (lo hacen Joaquín y Antonella) |

---

## 1. Microservicios (IE 3.1.2)

Carpeta nueva `backend/`, con dos microservicios en Spring Boot 4.1.2 compilados para Java 17 (corren con cualquier Java 17 o más nuevo, como el que trae Android Studio). Cómo ejecutarlos y la tabla completa de endpoints están en [backend/README.md](backend/README.md).

| Microservicio | Puerto | Qué maneja |
|---|---|---|
| `usuarios` | 8081 | Registro, login, cuentas de local que crea el Admin, activar o desactivar, contraseña temporal, "Mi cuenta" y perfil del restaurante |
| `promociones` | 8082 | Promociones (con 2x1, 3x2, combos y vigencia programada), canjes de vouchers y perfil de consumo |

**Por qué dos y no uno:** cada uno tiene una responsabilidad y su propia base de datos. Si uno se cae, el otro sigue respondiendo.

**Lo nuevo en cada uno:**
- **Base de datos:** H2 guardada en archivo, que arranca sin instalar nada. Hay un perfil `postgres` listo para PostgreSQL; la contraseña se entrega con la variable `DB_PASSWORD` y nunca va en el código.
- **Contraseñas con BCrypt** (hash con sal). Cumple el RNF-09, que estaba pendiente: el servidor nunca guarda ni devuelve una contraseña en texto plano.
- **Validaciones** con las mismas reglas de la app (correo, contraseña de 6+ caracteres con un número, precio, días, horario, vigencia). Los errores responden `{"error": "...", "campos": {...}}`.
- **Códigos HTTP correctos:** 201 al crear, 204 al borrar, 401 con clave incorrecta, 403 si el local está desactivado, 404 si no existe y 409 si el correo ya existe o el voucher ya se canjeó.
- **Swagger** en `/swagger-ui.html` de cada microservicio, para mostrar y probar los endpoints en la defensa.
- **Datos de prueba ficticios** que se cargan solos la primera vez: el mismo catálogo de 24 promociones de la app (con los mismos ids) y las cuentas `admin@correo.cl`, `trattoria@correo.cl` y `demo@correo.cl` / `demo1234`.

**Probado:** 34 llamadas de punta a punta (login bien, mal y desactivado; registro; CRUD de cuentas, locales, restaurantes, promociones, canjes y perfil de consumo). Todas respondieron lo esperado.

## 2. App conectada a los microservicios (IE 3.1.3)

La app ya no guarda solo en el teléfono: **los datos viven en el servidor y Room queda como copia** para cuando no hay conexión.

| Acción en la app | Llamada al microservicio |
|---|---|
| Iniciar sesión | `POST /api/auth/login` (8081) |
| Crear cuenta | `POST /api/auth/registro` |
| Recuperar contraseña | `POST /api/auth/restablecer` |
| Mi cuenta: nombre, foto y contraseña | `PUT /api/usuarios/{email}` y `/clave` |
| Admin: crear local, activar o desactivar, restablecer clave | `POST /api/usuarios/locales`, `PATCH .../activo`, `PUT .../clave-temporal` |
| Local: editar su restaurante | `PUT /api/restaurantes/{email}` |
| Abrir la app: catálogo | `GET /api/promociones` (8082) |
| Local: publicar, editar, pausar y eliminar promociones | `POST`, `PUT`, `PATCH .../estado`, `DELETE /api/promociones/{id}` |
| Local: canjes por promoción | `GET /api/canjes/conteo?vendedor=` |
| Comprador: canjear un beneficio | `POST /api/canjes` |
| Comprador: mis vouchers y perfil de consumo | `GET /api/canjes` y `GET /api/preferencias` |
| Comprador: revisar una promoción (+1 al perfil) | `POST /api/preferencias/sumar` |

**Cómo se comporta:**
- **Login contra el servidor:** una cuenta que existe solo en el servidor puede entrar desde cualquier teléfono. Probado con `demo@correo.cl`, que no estaba en el teléfono.
- **Sin conexión:** si los microservicios no responden, la app sigue con los datos guardados y muestra el aviso "Sin conexión con el servidor". Lo que se haga sin conexión se sube después.
- **Datos que existían solo en el teléfono:** al conectarse se suben al servidor. Eso incluye promociones creadas por locales, vouchers canjeados y cuentas de comprador (al iniciar sesión).
- **Correos sin distinguir mayúsculas:** "Local@gmail.com" y "local@gmail.com" son la misma cuenta, tanto en la app como en el servidor.
- **Botones con estado de carga:** "Ingresando…", "Creando cuenta…" mientras responde el servidor. No se puede apretar dos veces.
- **HTTP sin cifrar solo hacia el computador local:** el `usesCleartextTraffic` global se reemplazó por `network_security_config.xml`, que solo permite `10.0.2.2` y `localhost`. Todo lo demás exige HTTPS (avance en el RNF-10).

**Probado en el emulador:**
- Login con una cuenta que existe solo en el servidor.
- Canje: aparece en el servidor con +4 puntos en el perfil (+1 al revisar y +3 al canjear).
- El local ve "1 canje" hecho desde otra cuenta.
- Pausar y reactivar se refleja en el servidor.
- Una promoción "Menu" creada en la app queda en el servidor con su precio exacto, y al eliminarla desaparece del servidor.

**Archivos principales:**
- `data/remote/CocinaApi.kt`: las interfaces Retrofit de los dos microservicios.
- `data/remote/Remoto.kt`: traduce los errores de red para que la app nunca se caiga.
- `data/AppState.kt`: ahora recibe las APIs por parámetro, lo que permite probarlo con MockK.

**Limitaciones conocidas:**
- **Cambiar el correo de acceso** se hace solo en el teléfono: no hay endpoint que mueva todo en los dos microservicios a la vez.
- **El código de "recuperar contraseña"** se sigue mostrando en pantalla, porque no hay servidor de correo.
- **`Local@gmail.com`,** una cuenta de prueba creada antes con contraseña "123", no se sube al servidor porque no cumple la regla nueva (6+ caracteres y un número). Igual puede entrar desde el teléfono donde se creó.

## 3. API externa: clima de Open-Meteo (IE 3.1.4)

El inicio del comprador muestra el **clima de hoy** donde está el usuario, con una sugerencia para la salida:

> **21°** Despejado en Valparaíso
> Máx 21° · Mín 13° · Lluvia 0%
> Día despejado: buen momento para una terraza con vista.
> Datos: Open-Meteo

- **API:** [Open-Meteo](https://open-meteo.com). Es gratis, no pide clave y sus datos tienen licencia CC BY 4.0, por eso la tarjeta dice "Datos: Open-Meteo".
- **Endpoint:** `GET https://api.open-meteo.com/v1/forecast?latitude=-33.05&longitude=-71.62&current=temperature_2m,weather_code,is_day&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=America/Santiago&forecast_days=1`
- **Consumo con Retrofit:** `data/remote/ClimaApi.kt` usa su propio cliente, separado de los microservicios.
- **Coherente con la app:** la sugerencia cambia con el clima.
  - Con lluvia o frío sugiere "busca una mesa bajo techo" o "un café o una cazuela".
  - Con calor sugiere terraza o algo fresco.
  - De noche dice "Buena noche para salir a comer".
- **No interfiere con los datos propios:** el clima no se guarda en Room ni pasa por los microservicios, que es lo que pide la pauta. Vive en su propio `ClimaViewModel` (`viewmodel/ClimaViewModel.kt`), el primer ViewModel del proyecto.
- **Privacidad (Ley 21.719):** antes de enviar las coordenadas a Open-Meteo se redondean a 2 decimales, unos 1 km. Así no sale la ubicación exacta del usuario.
- **Sin internet:** la tarjeta no aparece y el resto del inicio sigue igual.
- **Pruebas:** `ClimaTest` tiene 5 pruebas: códigos del clima, conversión de la respuesta y sugerencias.
- **Probado en el emulador:** muestra 21° y despejado, lo mismo que responde la API consultada directamente.

## 4. Pruebas unitarias (IE 3.2.1)

**88 pruebas, todas pasan.** Cubren el 95,6% de las líneas de la lógica, el 92,3% de las instrucciones y el 80,6% de las ramas (if/when), medido con Jacoco.

| Archivo de prueba | Qué prueba | Pruebas |
|---|---|---|
| `data/AppStateTest.kt` | La capa de datos (el "repository" de la app) con las dos APIs simuladas con **MockK** | 38 |
| `data/RecomendadorTest.kt` | Búsqueda específica: condiciones duras, % de match, una propuesta por restaurante, alternativas, vigencia y textos | 9 |
| `utils/ValidacionesTest.kt` | Validaciones de login, registro, contraseña nueva, promoción y nuevo local | 7 |
| `utils/FechasTest.kt` | Horas disponibles, fechas del calendario y textos de fecha | 9 |
| `viewmodel/ClimaViewModelTest.kt` | ViewModel de la API externa con Open-Meteo simulado con MockK | 6 |
| `data/ClimaTest.kt` | Códigos del clima, conversión de la respuesta y sugerencias | 8 |
| `data/PromocionProgramadaTest.kt` | 2x1, 3x2, combos y vigencia de las promociones programadas | 6 |
| `data/remote/RemotoTest.kt` | Traducción de errores: 401, 409, sin red, JSON raro y cancelación | 5 |

**Herramientas:**
- **JUnit 4:** el marco de las pruebas.
- **MockK:** simula los microservicios, Open-Meteo y la base de datos.
- **kotlinx-coroutines-test:** controla los hilos para que todo corra en orden.
- **Jacoco:** mide la cobertura.

**Casos que se prueban en `AppStateTest`:**
- **Arranque:** sin servidor (siembra los datos y avisa) y con servidor (sube las promociones hechas sin conexión y trae las cuentas de local).
- **Login:**
  - Con servidor: guarda la cuenta y trae los vouchers y el perfil de consumo.
  - Errores: clave incorrecta (401), local desactivado (403) y otro error del servidor.
  - Sin conexión: entra con la cuenta guardada en el teléfono.
  - Cuenta que solo existe en el teléfono: se sube al servidor.
- **Registro:** con servidor, correo tomado (409) y sin conexión.
- **Admin:** crear local, desactivarlo (sus promociones se ocultan) y contraseña temporal.
- **Mi cuenta y recuperación:** recuperar contraseña con código, editar el perfil y cambiar la clave, incluida la temporal.
- **Canjes:** con servidor, sin conexión y sin sesión, con el +1 y +3 del perfil de consumo.
- **Local:** publicar (el servidor asigna el id), publicar sin conexión, editar, pausar, eliminar y editar su restaurante.
- **Catálogo:** "Para ti", filtros, cercanía, marea de horas, búsqueda específica y perfil público del restaurante.

**Qué se mide y qué no:**
- **Se mide la lógica:** carpetas `data/`, `utils/` y `viewmodel/`.
- **No se mide la interfaz:** pantallas Compose, tema y navegación.
- **Tampoco se mide** el código que genera Room (DAOs) ni lo que solo usa APIs del teléfono: GPS, notificaciones, fotos y la construcción de los clientes HTTP.
- **Excepción dentro de la lógica:** `cambiarCorreo` usa una transacción real de Room, así que se probaría con una prueba instrumentada en el emulador.

**Cambio para poder probar:** `AppState` ahora recibe por parámetro sus dos APIs y los hilos (`io` y `main`). La app usa los valores reales y las pruebas usan los simulados.

**Cómo correrlas:**

```
gradlew jacocoLogica
```

El reporte queda en `app/build/reports/jacoco/jacocoLogica/html/index.html`. Sirve para la captura de la cobertura en la defensa.

## 5. APK firmado (IE 3.3.1)

La app queda lista para publicar, como si fuera a Play Store.

| Archivo | Qué es |
|---|---|
| `keystore/cocinacapital.jks` | La llave de firma: RSA de 2048 bits, alias `cocinacapital`, válida 10.000 días (unos 27 años) |
| `keystore.properties` | Ruta de la llave, alias y contraseñas. Lo lee `app/build.gradle.kts` |
| `app/build.gradle.kts` | `signingConfigs { release }`, y el tipo `release` firma con esa llave |
| `entrega/CocinaCapital-1.0-release.apk` | El **APK firmado**, el que pide la pauta |
| `entrega/CocinaCapital-1.0-release.aab` | El **Android App Bundle** firmado, el formato que exige Play Store para publicar |

- **Certificado:** `CN=Cocina Capital, OU=DSY1105, O=Duoc UC, L=Vina del Mar, ST=Valparaiso, C=CL`.
- **Huella SHA-256:** `4733f212640b5160dd2fa2d4ff9a205b4c90c970c442f733442c7d6d76e24aac`.
- **Verificado:**
  - `apksigner verify` confirma la firma del APK (esquema v2).
  - `jarsigner -verify` confirma el AAB.
  - El release no es depurable, como corresponde a una versión publicable.
- **Probado instalado (23-nov):** en el emulador se desinstaló la versión debug y se instaló `CocinaCapital-1.0-release.apk`. Con los microservicios encendidos, el login con `demo@correo.cl` (cuenta que solo existe en el servidor), el clima de Open-Meteo, el canje y el voucher con QR funcionaron sin cierres.
- **Ojo para seguir programando:** en el emulador quedó la versión release. Android Studio pedirá desinstalarla para correr la debug, porque está firmada con otra llave. Eso borra los datos del emulador.
- **La contraseña** se generó al azar y está solo en `keystore.properties`.
  - Se sube al repositorio porque la pauta pide la llave "junto a su configuración".
  - En una app real, la llave y las contraseñas **no** se suben: se guardan aparte (por ejemplo, con Play App Signing).

**Cómo volver a generarlos:**

```
gradlew assembleRelease bundleRelease
```

También desde Android Studio: **Build → Generate Signed Bundle / APK**, eligiendo `keystore/cocinacapital.jks`.

**Captura para el README:** la pauta pide una captura del APK firmado y del .jks. Sirven la carpeta `entrega/` y `keystore/` en el explorador, junto con la salida de este comando:

```
apksigner verify --print-certs entrega/CocinaCapital-1.0-release.apk
```

**Repositorio:** se agregó un `.gitignore` que deja fuera lo compilado (`build/`, `target/`), las bases H2 de prueba y `local.properties`. La llave, su configuración y los archivos de `entrega/` **sí** se suben.

## Cómo probar todo junto

1. Levantar los dos microservicios (ver `backend/README.md`).
2. Abrir el emulador y ejecutar la app desde Android Studio.
3. Entrar con `demo@correo.cl` / `demo1234`: es una cuenta que solo existe en el servidor.
4. Para mostrar los datos en la defensa: Swagger (`http://localhost:8082/swagger-ui.html`) o la consola H2 (`http://localhost:8082/h2-console`).

> Desde un **celular físico** la app no llega a `10.0.2.2`. Hay que cambiar las URLs de `ApiConfig` (en `CocinaApi.kt`) por la IP del computador en la red Wi-Fi, y agregar esa IP en `network_security_config.xml`.

## 6. Kit para presentar en otro PC

La presentación es en un PC de la universidad, así que se armó la carpeta `presentacion/` para copiar al pendrive. Con ella no hay que compilar nada; basta con Java (el que trae Android Studio) y un emulador:

| Archivo | Para qué |
|---|---|
| `1-INICIAR-BACKEND.bat` | Busca Java 17+ solo (JAVA_HOME, Android Studio, JDK instalados o PATH), avisa si los puertos 8081/8082 están ocupados, levanta los dos microservicios en ventanas aparte y espera a que respondan |
| `2-INSTALAR-APP.bat` | Busca adb e instala el APK firmado en el emulador abierto. Si ya hay otra versión firmada con otra llave, ofrece desinstalarla |
| `LEEME.txt` | Pasos, cuentas demo, qué mostrar por cada indicador de la pauta y qué hacer si algo falla |
| `backend/` | Los dos .jar ya compilados |
| `app/` | APK y AAB release firmados |
| `firma/` | `cocinacapital.jks` (sin la contraseña) |
| `informes/` | Informe HTML de las 88 pruebas y de cobertura, por si allá no se pueden correr |
| `codigo/` | El proyecto en .zip, sin carpetas de compilación, por si no hay internet para abrir GitHub |

- Los microservicios ahora se compilan para **Java 17** (`java.version` en los `pom.xml`), así funcionan aunque Android Studio de la universidad sea más antiguo. Se verificó que las clases quedan en versión 61 (Java 17).
- `presentacion/` está en `.gitignore`: los .jar pesan ~60 MB cada uno y se regeneran con `mvnw package`.
- Probado el 23-nov copiando el kit a una carpeta vacía: el backend partió con bases nuevas (24 promos, login demo OK). El instalador detectó bien que el emulador tenía la versión debug con otra firma.

## 7. Emulador actualizado a la última versión (23-nov, noche)

**Qué pasó.** Al revisar la app con la cuenta de Joaquín no aparecía "Editar mi cuenta", y el registro dejaba elegir "Tipo de cuenta" (Comprador o Local). No era un error del código: el emulador había vuelto a una **versión antigua de la app**, una debug instalada el 27-oct y actualizada por última vez el 22-nov, antes de los cambios de cuentas. Se comprobó sacando el APK instalado del emulador. No tenía las pantallas de Admin, "Mi cuenta", Local ni clima, y sí tenía el texto "Tipo de cuenta".

**Por qué.** Para probar el kit, el emulador se abrió con la opción `-no-snapshot-save`, que no guarda los cambios al cerrarse. Al apagarse volvió a su último estado guardado, que tenía esa versión vieja, y se perdió el APK firmado instalado ese mismo día.

**Qué se hizo:**
1. Se confirmó que `entrega/CocinaCapital-1.0-release.apk` es la última versión: es más nuevo que todos los archivos de la app y trae "Editar mi cuenta", el panel del Admin, el clima de Open-Meteo y la Marea de horas, y ya no trae "Tipo de cuenta". Es idéntico al de `presentacion/app/`.
2. Se reinició el emulador en modo normal, que sí guarda los cambios.
3. Se desinstaló la versión vieja y se instaló el **APK firmado** (no depurable). Ahora el emulador tiene la versión de presentación.
4. Se levantaron los microservicios desde los .jar del kit y se probó la app:
   - **Registro:** solo para compradores, con el aviso "¿Tienes un restaurante? Cocina Capital verifica cada local…".
   - **Comprador** (`demo@correo.cl`): clima real (20°, despejado), "¿A qué hora sales?", y en la pestaña Perfil, "Editar mi cuenta" (foto, nombre, correo y contraseña).
   - **Admin** (`admin@correo.cl`): panel con "Crear cuenta de local", activar o desactivar locales y restablecer contraseña.
   - **Local** (`trattoria@correo.cl`): Mi restaurante, Promociones y Cuenta, con "Editar mi cuenta".
   - Ningún cierre de la app. Los 4 errores que aparecen en el registro del emulador son del 27-oct, de la versión vieja.
5. Se apagó el backend de prueba. El emulador quedó abierto en modo normal: al cerrarlo con la X guarda su estado, con la versión nueva incluida.

**Consecuencias:**
- **Cuentas creadas en el teléfono:** las cuentas hechas en la versión vieja (como la de Joaquín) vivían solo en el teléfono, porque esa versión no se conectaba al servidor. Se perdieron al desinstalar. Hay que crearlas de nuevo desde "Crear cuenta"; ahora quedan guardadas también en el microservicio de usuarios.
- **Cuentas de local:** una cuenta de local ya no se crea desde el registro. Se entra como `admin@correo.cl` → "Crear cuenta de local".
- **Android Studio:** si le dan **Run**, pedirá desinstalar la versión firmada, porque usa otra llave. Para presentar, usen el APK firmado.
- **No cambió nada en el código ni en el APK.** Solo se actualizó lo que había instalado en el emulador.

## 8. Prueba final de punta a punta (23-nov, noche)

Prueba completa con el **APK firmado** instalado en el emulador y el backend levantado con `1-INICIAR-BACKEND.bat` desde una copia limpia del kit:

| Qué se probó | Resultado |
|---|---|
| Pruebas unitarias de la app (`testDebugUnitTest`, forzando que todo se vuelva a ejecutar) | 88 de 88 OK |
| Backend (`mvnw test`) | Compila y pasa. Los microservicios no tienen pruebas propias; la pauta las pide en la app |
| `1-INICIAR-BACKEND.bat` | Encontró Java, levantó los dos servicios y avisó "LISTO" |
| Registro de comprador nuevo (`prueba.final@correo.cl`) | Se guarda en el servidor con rol Comprador |
| Login, clima, "¿A qué hora sales?" | Clima real de Open-Meteo. Como en el emulador eran las 22:17, mostró las horas de mañana |
| Arma tu salida → 5 propuestas → Elegir → Canjear | Voucher CC-036899 con QR. En el servidor quedó el canje y la preferencia Vegana con 4 puntos |
| Admin crea el local "Sushi Prueba" | Aparece en el servidor con rol Vendedor |
| Login del local nuevo → "Nueva promoción" tipo **Combo** ($12.000 → $8.990, Lun–Jue, 1 mes) | Se publica sin cerrarse y llega al servidor (id 901, etiqueta Combo) |
| Pausar → Historial → Eliminar | El servidor refleja la promo pausada y luego eliminada. Quedan las 24 promos originales |
| Registro de errores del emulador | 0 cierres. Solo avisos del sistema al forzar el cierre de la app durante la prueba |

Al terminar se apagó el backend y se borró la copia del kit. En el emulador quedaron las cuentas de prueba `prueba.final@correo.cl` y `sushi.prueba@correo.cl`, que son ficticias. El PC de la universidad parte limpio.
