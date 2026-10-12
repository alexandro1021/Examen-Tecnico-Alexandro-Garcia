# Middleware TV Maze

API en Java que intermedia [TV Maze](https://www.tvmaze.com/api).

## Punto A — búsqueda de shows

`GET /api/search?search_query={texto}`

Consulta `https://api.tvmaze.com/search/shows?q={texto}` y devuelve un arreglo con:

| Campo | Origen |
| --- | --- |
| `id` | `show.id` |
| `name` | `show.name` |
| `channel` | `network.name`, o `webChannel.name` si no hay network |
| `summary` | `show.summary` |
| `genres` | `show.genres` |

```bash
curl "http://localhost:8080/api/search?search_query=girls"
```

## Punto B — detalle de un show

`GET /api/show/{show_id}`

Consulta `https://api.tvmaze.com/shows/{show_id}` y devuelve el objeto show completo. Si TV Maze responde 404, la API responde 404.

```bash
curl "http://localhost:8080/api/show/139"
```

## Ejecutar

Requiere Java 21.

```bash
./mvnw spring-boot:run
```

La documentación interactiva queda en `http://localhost:8080/swagger-ui.html`.

## Pruebas

```bash
./mvnw verify
```
