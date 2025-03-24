package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    //private final AvatarRepository avatarRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        //this.avatarRepository = avatarRepository;
    }

    // Метод для создания нового студента
    public Student createStudent(String name, int age) {
        Student student = new Student(name, age);
        return studentRepository.save(student);
    }

    // Метод для получения студента по ID
    public Student getStudent(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        return student.orElse(null); // Возвращаем null, если студент не найден
    }

    // Метод для обновления информации о студенте
    public boolean updateStudent(Long id, String name, int age) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            student.setName(name);
            student.setAge(age);
            studentRepository.save(student); // Сохраняем обновленного студента
            return true;
        }
        return false;
    }

    // Метод для удаления студента по ID
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Метод для получения всех студентов
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Метод для поиска студентов по возрасту
    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findAll()
                .stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }

    // Метод для получения всех студентов, которые в диапазоне по возрасту
    public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    // Метод для получения факультета студента
    public Faculty getStudentFaculty(Long studentId) {
       return studentRepository.findFacultyById(studentId)
               .orElseThrow(() -> new RuntimeException("Faculty not found for student ID " + studentId));
    }
}
