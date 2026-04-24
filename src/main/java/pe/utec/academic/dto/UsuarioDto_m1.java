package pe.utec.academic.dto;

import lombok.*;

//Estos datos son jalados por el MS1 y solo jala los datos necesarios para alimentar la entidad ProfesorCurso
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