# BookPoint · Microservicio de Cupones (`ms-cupones`)

Microservicio encargado de la gestión de cupones de descuento dentro del sistema **BookPoint**. Expone una API REST con soporte **HATEOAS** y es consumido por otros microservicios (como `ms-carro` y `ms-pedido`) para aplicar descuentos sobre el total de una compra.


## 🛠️ Tecnologías

- **Java 17+**
- **Spring Boot 4**
- **Spring Web (MVC)**
- **Spring HATEOAS** — respuestas con `EntityModel` / `CollectionModel`
- **Spring Data JPA**
- **MySQL** (entorno de producción/desarrollo)
- **H2** (base de datos en memoria para tests)
- **SpringDoc OpenAPI / Swagger** — documentación de la API
- **JaCoCo** — reportes de cobertura de tests
- **JUnit 5 + Mockito** — pruebas unitarias e integración

---

## 🏗️ Rol en la arquitectura

`ms-cupones` es un servicio **proveedor**: mantiene el catálogo de cupones y responde a las consultas de otros microservicios que necesitan validar o aplicar un descuento.

```
Cliente → Gateway (8080) → ms-cupones (8085)
                                ▲
                                │ (consultan vía RestTemplate)
                       ms-carro / ms-pedido
```

---

## ✅ Requisitos previos

- JDK 17 o superior
- Maven 3.8+
- MySQL en ejecución (para el perfil por defecto)

---

## ⚙️ Configuración

### `src/main/resources/application.properties`

```properties
spring.application.name=ms-cupones
server.port=8085

# Base de datos (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/bookpoint_cupones
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### `src/test/resources/application.properties` (H2 en memoria)

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 🌐 Acceso vía API Gateway

En producción no se accede directo al puerto `8085`, sino a través del gateway en el puerto `8080`:

```
GET http://localhost:8080/api/v1/cupones
```

Ruta configurada en el gateway:

```yaml
- id: ms-cupones
  uri: http://localhost:8085
  predicates:
    - Path=/api/v1/cupones,/api/v1/cupones/**
```

---

## 📡 Endpoints

Base: `/api/v1/cupones`.

| Método | Endpoint                      | Descripción                          | Código éxito |
|--------|-------------------------------|--------------------------------------|--------------|
| `GET`    | `/api/v1/cupones`             | Listar todos los cupones             | `200 OK`     |
| `GET`    | `/api/v1/cupones/{codigo}`    | Buscar un cupón por su código        | `200 OK`     |
| `POST`   | `/api/v1/cupones`             | Crear un nuevo cupón                 | `201 Created`|
| `PUT`    | `/api/v1/cupones/{codigo}`    | Actualizar un cupón existente        | `200 OK`     |
| `DELETE` | `/api/v1/cupones/{codigo}`    | Eliminar un cupón                    | `204 No Content` |


### Ejemplo de respuesta (HATEOAS)

Listado (`GET /api/v1/cupones`):

```json
{
  "_embedded": {
    "cuponDescuentoList": [
      {
        "codigo": "VERANO2026",
        "descuento": 15,
        "_links": {
          "self": { "href": "http://localhost:8085/api/v1/cupones/VERANO2026" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8085/api/v1/cupones" }
  }
}
```

## 🗂️ Modelo de datos — `CuponDescuento`


| Campo             | Tipo        | Descripción                              |
|-------------------|-------------|------------------------------------------|
| `codigo`          | `String`    | Código único del cupón (PK)              |
| `descuento`       | `Integer`   | Porcentaje o monto de descuento          |
| `descripcion`     | `String`    | Descripción del cupón                    |
| `fechaVencimiento`| `LocalDate` | Fecha de expiración del cupón            |
| `activo`          | `Boolean`   | Indica si el cupón está vigente          |

---

- **Pruebas unitarias:** `@ExtendWith(MockitoExtension.class)` con `@Mock` / `@InjectMocks`.
- **Pruebas de integración:** `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")`.
- El `ObjectMapper` en los tests debe inyectarse con `@Autowired` (no `new ObjectMapper()`) para que serialice correctamente los `LocalDate`.

---

## 📁 Estructura del proyecto

```
ms-cupones/
├── src/
│   ├── main/
│   │   ├── java/com/bookpoint/cupones/
│   │   │   ├── controller/
│   │   │   │   └── CuponController.java
│   │   │   ├── model/
│   │   │   │   └── CuponDescuento.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── CuponesApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/bookpoint/cupones/
│       └── resources/
│           └── application.properties
└── pom.xml
```

## 👤 Autor

Proyecto **BookPoint** — Microservicio de Cupones.
