package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    //Получение списка студентов в определенном диапазоне
    List<Student> findByAgeBetween(int minAge, int maxAge);

    //Получение факультета студента по id студента
    @Query("SELECT s.faculty FROM Student s WHERE s.id = :studentId")
    Optional<Faculty> findFacultyById(@Param("studentId") Long studentId);

    // Получение количества всех студентов
    @Query("SELECT COUNT(s) FROM Student s")
    long countAllStudents();

    // Получение среднего возраста студентов
    @Query("SELECT AVG(s.age) FROM Student s")
    double findAverageAge();

    // Получение последних 5 студентов (по идентификатору)
    @Query("SELECT s FROM Student s ORDER BY s.id DESC")
    List<Student> findTop5ByOrderByIdDesc(Pageable pageable);

}

