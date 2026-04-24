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

@Component
@Slf4j
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Value("${ms1.url}") String ms1Url) {
        this.webClient = WebClient.builder()
            .baseUrl(ms1Url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    /**
     * Obtiene el perfil de un usuario por su UUID.
     * Devuelve Optional.empty() si no existe o si el MS1 falla.
     *
     * @param usuarioId UUID del usuario en MS1
     * @param bearerToken token JWT del request actual (para reenviar al MS1)
     */
    public Optional<UsuarioMs1Dto> obtenerUsuario(String usuarioId, String bearerToken) {
        try {
            UsuarioMs1Dto usuario = webClient.get()
                .uri("/usuarios/{id}", usuarioId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
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
            log.warn("[MS1 CLIENT] No se pudo obtener usuario {}: {}", usuarioId, ex.getMessage());
            return Optional.empty();
        }
    }
    //Verifica si el usuario tiene rol profesor 
    public boolean esProfesorValido(String usuarioId, String bearerToken) {
        return obtenerUsuario(usuarioId, bearerToken)
            .map(u -> "PROFESOR".equals(u.getRol()) && Boolean.TRUE.equals(u.getActivo()))
            .orElse(false);
    }
}