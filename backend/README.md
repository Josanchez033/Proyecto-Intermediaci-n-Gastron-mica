# Microservicios de Cocina Capital

Dos microservicios en Spring Boot 4.1 (compilados para Java 17; corren con Java 17 o más nuevo) que usa la app Android:

| Microservicio | Puerto | Qué maneja |
|---|---|---|
| `usuarios` | 8081 | Cuentas de comprador, local y admin; login con contraseñas en BCrypt; perfil de cada restaurante |
| `promociones` | 8082 | Promociones de los locales, canjes de beneficios y perfil de consumo de cada comprador |

Cada uno tiene su propia base de datos. Por defecto es H2 guardada en archivo (`./data`), así que no hay que instalar nada. También funcionan con PostgreSQL (ver más abajo).

## Cómo ejecutar

En dos consolas, una por microservicio:

```
cd backend/usuarios
mvnw spring-boot:run
```

```
cd backend/promociones
mvnw spring-boot:run
```

Al arrancar por primera vez:
- `usuarios` crea las cuentas de prueba (datos ficticios): `admin@correo.cl` / `admin1234`, `trattoria@correo.cl` / `trattoria123` y `demo@correo.cl` / `demo1234`.
- `promociones` carga el catálogo de ejemplo de la app (`catalogo.json`, 24 promociones con los mismos ids).

Documentación interactiva (Swagger):
- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html

Consola de la base de datos H2: http://localhost:8081/h2-console y http://localhost:8082/h2-console (JDBC URL `jdbc:h2:file:./data/usuarios` o `./data/promociones`, usuario `sa`, sin contraseña).

### Con PostgreSQL

1. Crear las bases: `CREATE DATABASE cocina_usuarios;` y `CREATE DATABASE cocina_promociones;`
2. Dejar la contraseña de PostgreSQL en la variable de entorno `DB_PASSWORD` (no va en el código).
3. Ejecutar con el perfil `postgres`: `mvnw spring-boot:run -Dspring-boot.run.profiles=postgres`

## Endpoints

### usuarios (8081)

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/auth/registro` | Crea una cuenta de comprador (201; 409 si el correo existe) |
| POST | `/api/auth/login` | Inicia sesión (401 si no calza; 403 si el local está desactivado) |
| GET | `/api/auth/existe?email=` | Dice si un correo ya tiene cuenta |
| POST | `/api/auth/restablecer` | Recuperar contraseña de un comprador (la app ya verificó el código) |
| GET | `/api/usuarios?rol=` | Lista cuentas (todas o de un rol) |
| GET | `/api/usuarios/{email}` | Una cuenta |
| PUT | `/api/usuarios/{email}` | Edita nombre y foto ("Mi cuenta") |
| PUT | `/api/usuarios/{email}/clave` | Cambia la contraseña (pide la actual) |
| DELETE | `/api/usuarios/{email}` | Elimina la cuenta |
| GET | `/api/usuarios/locales` | Locales con su restaurante (panel del Admin) |
| POST | `/api/usuarios/locales` | El Admin crea un local y su restaurante |
| PATCH | `/api/usuarios/{email}/activo` | Activa o desactiva un local |
| PUT | `/api/usuarios/{email}/clave-temporal` | El Admin restablece la contraseña de un local |
| GET | `/api/restaurantes` | Perfiles de restaurante |
| GET | `/api/restaurantes/{email}` | Perfil de un restaurante |
| PUT | `/api/restaurantes/{email}` | El local edita su perfil |

### promociones (8082)

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/promociones` | Catálogo (`?vendedor=` para un local, `?activas=true` solo visibles) |
| GET | `/api/promociones/{id}` | Una promoción |
| POST | `/api/promociones` | Publica una promoción (valida precio, días, horario y vigencia) |
| PUT | `/api/promociones/{id}` | Edita una promoción |
| PATCH | `/api/promociones/{id}/estado` | Pausa o reactiva |
| DELETE | `/api/promociones/{id}` | Elimina |
| GET | `/api/canjes?usuario=` | Vouchers de un comprador |
| POST | `/api/canjes` | Canjea un beneficio (409 si ya lo canjeó; suma +3 al perfil) |
| DELETE | `/api/canjes/{id}` | Anula un canje |
| GET | `/api/canjes/conteo?vendedor=` | Canjes por promoción de un local |
| GET | `/api/preferencias?usuario=` | Perfil de consumo de un comprador |
| POST | `/api/preferencias/sumar` | Suma puntos a una categoría (+1 al revisar) |

`/api/ofertas` responde lo mismo que `/api/promociones` (es la ruta que usaba la app).

Los errores responden `{"error": "..."}`. Si faltan datos o son inválidos, también traen `"campos"` con el mensaje de cada uno.
