package pe.utec.academic.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.*;
import pe.utec.academic.dto.UsuarioMs1Dto;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Optional;

/**
 * Cliente HTTP para MS1 (ms-users).
 *
 * MODO MOCK (MS1_MOCK=true):
 *   No llama al MS1. Devuelve datos fake para poder probar
 *   el MS2 de forma TOTALMENTE INDEPENDIENTE sin necesitar
 *   el MS1 corriendo.
 *
 * MODO NORMAL (MS1_MOCK=false, por defecto):
 *   Llama a GET /usuarios/:id del MS1 con el token JWT.
 *   Si MS1 no responde en 5s, devuelve Optional.empty()
 *   sin fallar (nombre queda como "—").
 */
@Component
@Slf4j
public class UserServiceClient {

    private final WebClient webClient;
    private final boolean   mockEnabled;

    public UserServiceClient(
            @Value("${ms1.url}") String ms1Url,
            @Value("${ms1.mock-enabled:false}") boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
        this.webClient = WebClient.builder()
            .baseUrl(ms1Url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE,
                           MediaType.APPLICATION_JSON_VALUE)
            .build();

        if (mockEnabled) {
            log.warn("⚠️  MS1 en MODO MOCK — los nombres de profesores son datos ficticios");
        }
    }

    /**
     * Obtiene el perfil de un usuario desde MS1.
     * En modo mock devuelve datos ficticios sin llamar a nadie.
     */
    public Optional<UsuarioMs1Dto> obtenerUsuario(String usuarioId,
                                                   String bearerToken) {
        // ── MODO MOCK ─────────────────────────────────────────
        if (mockEnabled) {
            return Optional.of(buildMockUsuario(usuarioId));
        }

        // ── MODO REAL ─────────────────────────────────────────
        try {
            UsuarioMs1Dto usuario = webClient.get()
                .uri("/usuarios/{id}", usuarioId)
                .header(HttpHeaders.AUTHORIZATION,
                        bearerToken != null ? bearerToken : "")
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError(),
                    response -> Mono.empty()
                )
                .bodyToMono(UsuarioMs1Dto.class)
                .timeout(Duration.ofSeconds(5))
                .block();

            return Optional.ofNullable(usuario);

        } catch (Exception ex) {
            log.warn("[MS1] No se pudo obtener usuario {}: {}",
                     usuarioId, ex.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Verifica si el usuario existe en MS1 y tiene rol PROFESOR.
     * En modo mock siempre retorna true para facilitar pruebas.
     */
    public boolean esProfesorValido(String usuarioId, String bearerToken) {
        if (mockEnabled) {
            log.info("[MS1-MOCK] esProfesorValido({}) → true", usuarioId);
            return true;
        }
        return obtenerUsuario(usuarioId, bearerToken)
            .map(u -> "PROFESOR".equals(u.getRol())
                   && Boolean.TRUE.equals(u.getActivo()))
            .orElse(false);
    }

    // ── Datos mock por UUID conocido ──────────────────────────
    private UsuarioMs1Dto buildMockUsuario(String id) {
        return switch (id) {
            case "00000000-0000-0000-0000-000000000001" ->
                UsuarioMs1Dto.builder()
                    .id(id).nombre("Juan").apellido("Pérez")
                    .correo("juan.perez@utec.edu.pe")
                    .rol("PROFESOR").activo(true)
                    .foto("https://ui-avatars.com/api/?name=Juan+Perez&background=6366F1&color=fff")
                    .build();
            case "00000000-0000-0000-0000-000000000002" ->
                UsuarioMs1Dto.builder()
                    .id(id).nombre("Ana").apellido("Torres")
                    .correo("ana.torres@utec.edu.pe")
                    .rol("PROFESOR").activo(true)
                    .foto("https://ui-avatars.com/api/?name=Ana+Torres&background=EC4899&color=fff")
                    .build();
            case "00000000-0000-0000-0000-000000000003" ->
                UsuarioMs1Dto.builder()
                    .id(id).nombre("Pedro").apellido("Ruiz")
                    .correo("pedro.ruiz@utec.edu.pe")
                    .rol("PROFESOR").activo(true)
                    .foto("https://ui-avatars.com/api/?name=Pedro+Ruiz&background=10B981&color=fff")
                    .build();
            default ->
                UsuarioMs1Dto.builder()
                    .id(id).nombre("Profesor").apellido("UTEC")
                    .correo("profesor@utec.edu.pe")
                    .rol("PROFESOR").activo(true)
                    .foto("https://ui-avatars.com/api/?name=Profesor+UTEC&background=F59E0B&color=fff")
                    .build();
        };
    }
}
