package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.model.Student;

import java.util.List;

@RestController
@RequestMapping("/students")
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

    // Эндпоинт для получения количества всех студентов
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalStudents() {
        long count = studentService.getTotalStudents();
        return ResponseEntity.ok(count);
    }

    // Эндпоинт для получения 5 последних студентов
    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> getLastFiveStudents() {
        List<Student> students = studentService.getLastFiveStudents();
        return ResponseEntity.ok(students);
    }

    // Эндпоинт для получения всех имен всех студентов, чье имя начинается с буквы А
    @GetMapping("/names-starting-with-a")
    public ResponseEntity<List<String>> getNamesStartingWithA() {
        List<String> names = studentService.getNamesStartingWithA();
        return ResponseEntity.ok(names);
    }

    // Эндпоинт для получения среднего возраста студентов
    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge() {
        double averageAge = studentService.getAverageAge();
        return ResponseEntity.ok(averageAge);
    }

    // Эндпоинт для вывода в консоль имен всех студентов в параллельном режиме
    @GetMapping("/print-parallel")
    public String printStudentsInParallel() {
        List<Student> students = studentService.getAllStudents();

        if (students.size() < 6) {
            return "Not enough students (minimum 6 required)";
        }

        // Первые два имени — основной поток
        System.out.println("Main Thread:");
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        // Поток 1 — 3-й и 4-й студенты
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1:");
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        // Поток 2 — 5-й и 6-й студенты
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2:");
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        return "Printed 6 student names in parallel (check console)";
    }

    // Синхронизированный метод для вывода имени студента
    private synchronized void printStudentName(String name) {
        System.out.println(name);
    }

    @GetMapping("/print-synchronized")
    public String printStudentsSynchronized() {
        List<Student> students = studentService.getAllStudents();

        if (students.size() < 6) {
            return "Not enough students (minimum 6 required)";
        }

        // Первые два имени — основной поток
        System.out.println("Main Thread:");
        printStudentName(students.get(0).getName());
        printStudentName(students.get(1).getName());

        // Поток 1 — 3-й и 4-й студенты
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1:");
            printStudentName(students.get(2).getName());
            printStudentName(students.get(3).getName());
        });

        // Поток 2 — 5-й и 6-й студенты
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2:");
            printStudentName(students.get(4).getName());
            printStudentName(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        return "Printed 6 student names in synchronized mode (check console)";
    }
}
