package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    // Метод для поиска факультетов по имени или цвету с игнорированием регистра
    List<Faculty> findByNameIgnoreCaseContainingOrColorIgnoreCaseContaining(String name, String color);

    // Метод для поиска студентов по ID факультета
    //List<Student> findStudentsByFacultyId(Long facultyId);
    List<Student> findStudentsById(Long id);

}
