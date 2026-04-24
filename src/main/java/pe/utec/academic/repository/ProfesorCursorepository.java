package pe.utec.academic.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.utec.academic.model.ProfesorCurso;
import java.util.List;
import java.util.Optional;

public interface ProfesorCursoRepository extends JpaRepository<ProfesorCurso, Integer> {

    // Lista de todos los profesores de un curso
    List<ProfesorCurso> findByCursoIdAndActivoTrue(Integer cursoId);

    // Lista de todos los cursos de un profesor
    List<ProfesorCurso> findByProfesorIdAndActivoTrue(String profesorId);

    // Lista de todos los cursos de un profesor en un semestre
    List<ProfesorCurso> findByProfesorIdAndSemestreAndActivoTrue(
        String profesorId, String semestre
    );

    boolean existsByProfesorIdAndCursoIdAndSemestre(
        String profesorId, Integer cursoId, String semestre
    );
    
    Optional<ProfesorCurso> findByIdAndActivoTrue(Integer id);

    @Query("""
        SELECT pc FROM ProfesorCurso pc
        JOIN pc.curso c
        JOIN c.carrera ca
        WHERE ca.id = :carreraId
        AND pc.activo = true
        AND (:semestre IS NULL OR pc.semestre = :semestre)
        """)
    List<ProfesorCurso> findByCarreraIdAndSemestre(
        @Param("carreraId") Integer carreraId,
        @Param("semestre")  String  semestre
    );
}