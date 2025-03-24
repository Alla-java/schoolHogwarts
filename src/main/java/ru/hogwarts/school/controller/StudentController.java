package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.model.Student;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Эндпоинт для создания нового студента
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student.getName(), student.getAge());
    }

    // Эндпоинт для получения студента по ID
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    // Эндпоинт для получения всех студентов
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // Эндпоинт для обновления информации о студенте
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        if (studentService.updateStudent(id, updatedStudent.getName(), updatedStudent.getAge())) {
            return studentService.getStudent(id);
        }
        return null;  // Возвращаем null, Spring автоматически вернёт 404, если студент не найден
    }

    // Эндпоинт для удаления студента по ID
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    // Эндпоинт для фильтрации студентов по возрасту
    @GetMapping("/age/{age}")
    public List<Student> getStudentsByAge(@PathVariable int age) {
        return studentService.getStudentsByAge(age);
    }

    // Эндпоинт для фильтрации студентов по возрасту
    @GetMapping("/age/range")
    public List<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.getStudentsByAgeRange(min, max);
    }

    //Эндпоинт для привязки студента к факультету
    @PutMapping("/{studentId}/faculty/{facultyId}")
    public ResponseEntity<Student> assignFacultyToStudent(
            @PathVariable Long studentId,
            @PathVariable Long facultyId) {

        Student updatedStudent = studentService.assignFacultyToStudent(studentId, facultyId);

        if (updatedStudent != null) {
            return ResponseEntity.ok(updatedStudent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Эндпоинт для получения факультета студента по его ID
    @GetMapping("/{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        return studentService.getStudentFaculty(id);
    }
}
