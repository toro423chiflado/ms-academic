package pe.utec.academic.dto;

import lombok.*;

/**
 * Representa la respuesta de GET /usuarios/:id del MS1.
 * Solo los campos que necesitamos para enriquecer ProfesorCurso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioMs1Dto {
    private String  id;
    private String  nombre;
    private String  apellido;
    private String  correo;
    private String  rol;
    private String  foto;
    private Boolean activo;
}
