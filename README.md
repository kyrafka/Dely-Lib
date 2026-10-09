# Dely Lib API

API REST para una aplicación de librería. Gestiona autenticación, catálogo, géneros, inventario, carritos y compras. El cliente Android se mantiene en un repositorio independiente: [dely-lib-appMobile](https://github.com/kyrafka/dely-lib-appMobile).

## Tecnologías

- Java 21 y Spring Boot 3.5.
- Spring Web, Spring Data JPA y Bean Validation.
- Oracle Database para ejecución; H2 para pruebas automatizadas.
- Maven Wrapper para compilar y verificar el proyecto.
- Springdoc OpenAPI para consultar la descripción de los endpoints.

## Componentes de la API

Las rutas principales utilizan el prefijo `/api/v1`.

| Recurso | Ruta base | Operaciones principales |
| --- | --- | --- |
| Autenticación | `/register`, `/login`, `/logout` | Registro, acceso y cierre de sesión |
| Libros | `/libros` | Catálogo, detalle e imágenes |
| Géneros | `/generos`, `/genero-libros` | Consulta y administración de géneros |
| Inventario | `/inventarios` | Existencias por libro |
| Carrito | `/carrito` | Consulta, actualización y eliminación de artículos |
| Compras | `/compras`, `/detalle-compras` | Pedidos y sus detalles |

Cuando el servicio está en ejecución, la interfaz OpenAPI se encuentra en `/swagger-ui/index.html` y el documento JSON en `/v3/api-docs`.

## Requisitos

- JDK 21.
- Una instancia Oracle con el esquema requerido por las entidades JPA.
- Variables de entorno para la conexión a la base de datos.

## Configuración local

La configuración versionada no contiene credenciales. Define los datos de la base de datos en el entorno antes de iniciar el servicio:

~~~powershell
$env:SPRING_DATASOURCE_URL = "jdbc:oracle:thin:@localhost:1521/FREEPDB1"
$env:SPRING_DATASOURCE_USERNAME = "usuario_local"
$env:SPRING_DATASOURCE_PASSWORD = "contraseña_local"
.\mvnw.cmd spring-boot:run
~~~

El servicio escucha en el puerto `9090` por defecto. El esquema debe existir y ser compatible: Hibernate utiliza `ddl-auto=validate` y no crea ni modifica tablas al arrancar.

El checkout permanece deshabilitado por defecto. Esta versión no incluye un proveedor de pagos ni endpoints de webhook. `.env.example` conserva únicamente una plantilla vacía para una posible integración futura; no contiene credenciales y no se carga automáticamente.

## Pruebas

~~~powershell
.\mvnw.cmd verify
~~~

Las pruebas de integración usan H2 en memoria y no requieren una conexión a Oracle.

## Seguridad y operación

- Las contraseñas se almacenan con BCrypt.
- Los tokens de sesión se generan aleatoriamente, se almacenan como hash y tienen vencimiento.
- Las sesiones vencidas se limpian mediante una tarea programada.
- Los precios y las cantidades de inventario se validan en el servidor durante la compra.
- Los errores HTTP no incluyen mensajes internos ni trazas en las respuestas públicas.
- Los límites del pool de conexiones, los hilos del servidor y el tamaño de las cargas se configuran mediante variables de entorno.
- El checkout está deshabilitado por defecto mediante `APP_CHECKOUT_ENABLED=false`.

La migración SQL de sesiones se encuentra en `database/migrations/20261009_harden_sessions.sql`. Revísala y ejecútala de acuerdo con el procedimiento de cambios de la base de datos; el arranque de la aplicación no la aplica automáticamente.

## Proyecto personal y créditos

Proyecto personal para portafolio. Integra cambios publicados en [glender222/Dely-Lib](https://github.com/glender222/Dely-Lib) y conserva la atribución a ese trabajo. Esta versión incorpora ajustes propios de seguridad y configuración, y no incluye integración de pagos ni credenciales privadas.
