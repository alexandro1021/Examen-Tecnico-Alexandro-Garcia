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

Antes de llamar a TV Maze busca el id en la colección `shows` de MongoDB. Si ya está guardado, devuelve ese objeto. Si no, consulta `https://api.tvmaze.com/shows/{show_id}`, guarda el JSON completo y después lo devuelve. Un 404 de TV Maze no se guarda.

```bash
curl "http://localhost:8080/api/show/139"
```

## MongoDB

La caché de shows y los comentarios se guardan en MongoDB. La URI se toma de `MONGODB_URI` y, si no está definida, usa `mongodb://localhost:27017/tvmaze`.

Con Docker:

```bash
docker compose up -d
```

### Instancia gratuita en MongoDB Atlas sin restricción de IP

1. Entra a [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register) y crea un proyecto.
2. Crea un cluster **M0 Free**.
3. En **Database Access**, crea un usuario con contraseña. Si la contraseña tiene caracteres especiales, codifícalos en la URI (`@` pasa a `%40`).
4. En **Network Access**, elige **Add IP Address** y luego **Allow Access from Anywhere** (`0.0.0.0/0`). El examen pide una instancia sin restricción de IP.
5. En **Connect → Drivers**, copia la cadena `mongodb+srv://...`.
6. Arranca la API con esa cadena y el nombre de base `tvmaze`:

```bash
export MONGODB_URI='mongodb+srv://USUARIO:PASSWORD@CLUSTER.mongodb.net/tvmaze?retryWrites=true&w=majority'
./mvnw spring-boot:run
```

`GET /actuator/health` debe mostrar `mongo` en estado `UP`.

## Ejecutar

Requiere Java 21.

```bash
./mvnw spring-boot:run
```

La documentación interactiva queda en `http://localhost:8080/swagger-ui.html`.

## Pruebas

MongoDB tiene que estar escuchando en `localhost:27017` (o define `MONGODB_URI`).

```bash
./mvnw verify
```
