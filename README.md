# MS2 — ms-academic

Microservicio académico de **UTEC Rate**.
Gestiona Carreras, Cursos y asignación Profesor–Curso.
**Consume MS1** para validar que un usuario tiene rol PROFESOR.

---

## ¿Qué necesitas instalado en tu laptop?

| Herramienta | Para qué | Descarga |
|---|---|---|
| **Docker Desktop** | Correr MySQL y correr el microservicio sin instalar Java ni MySQL | [docker.com/desktop](https://www.docker.com/products/docker-desktop/) |
| **Postman** | Probar los endpoints | [postman.com](https://www.postman.com/downloads/) |

> Con solo Docker Desktop y Postman puedes probar todo. No necesitas instalar Java, Maven ni MySQL.

---

## Levantar el MS2 de forma local

### Paso 1 — Clona o copia el proyecto

```bash
git clone https://github.com/toro423chiflado/ms-academic.git
cd ms-academic
```

### Paso 2 — Levanta MySQL y ms-academic con Docker

```bash
docker compose up -d --build  

# Esto descarga MySQL 8 y lo levanta en el puerto 3306
# Compila el proyecto usan Maven
# Crea las tablas de la base de datos usando JPA
# Inserta los datos del seed 

```

### Paso 3 — Verificar que esté corriendo

```bash
docker compose logs -f ms-academic

# Cuando veas esto, está listo:
# Started MsAcademicApplication in X.XXX seconds
```

También puedes abrir en el navegador:
- **`http://localhost:3002/health`** → debe responder `{ "status": "ok" }`
- **`http://localhost:3002/docs`** → Swagger UI interactivo

### Paso 4 — Detener

```bash
docker compose down          # detiene pero conserva los datos
docker compose down -v       # detiene y borra la BD (datos frescos)
```

---

## Endpoints disponibles

| Método | Ruta | Token | Descripción |
|---|---|---|---|
| GET | /health | No | Health check |
| GET | /docs | No | Swagger UI |
| GET | /carreras | No | Listar carreras activas |
| GET | /carreras/:id | No | Carrera por ID |
| POST | /carreras | Sí | Crear carrera |
| PUT | /carreras/:id | Sí | Actualizar carrera |
| DELETE | /carreras/:id | Sí | Desactivar carrera |
| GET | /cursos | No | Listar cursos (paginado, filtros) |
| GET | /carreras/:id/cursos | No | Cursos de una carrera |
| GET | /cursos/:id | No | Curso por ID |
| POST | /cursos | Sí | Crear curso |
| PUT | /cursos/:id | Sí | Actualizar curso |
| DELETE | /cursos/:id | Sí | Desactivar curso |
| GET | /cursos/:id/profesores | Sí | Profesores de un curso (enriquecido MS1) |
| GET | /profesores/:id/cursos | Sí | Cursos de un profesor |
| GET | /profesor-curso/:id | Sí | Asignación por ID |
| GET | /profesor-curso/:id/existe | No | Verifica si existe (para MS4) |
| POST | /profesor-curso | Sí | Asignar profesor a curso (valida en MS1) |
| DELETE | /profesor-curso/:id | Sí | Desactivar asignación |

---