package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.repository.FacultyRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
        this.facultyRepository = facultyRepository;
    }

    // Метод для создания нового студента
    public Student createStudent(String name, int age) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with name = {}, age = {}", name, age);

        Student student = new Student(name, age);
        Student saved = studentRepository.save(student);
        logger.debug("Student created with id = {}", saved.getId());
        return saved;
    }

    // Метод для получения студента по ID
    public Student getStudent(Long id) {
        logger.info("Was invoked method for get student by id");
        logger.debug("Fetching student with id = {}", id);

        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            logger.warn("No student found with id = {}", id);
        }
        return student.orElse(null);
    }

    // Метод для обновления информации о студенте
    public boolean updateStudent(Long id, String name, int age) {
        logger.info("Was invoked method for update student");
        logger.debug("Updating student with id = {}, name = {}, age = {}", id, name, age);

        Optional<Student> studentOptional = studentRepository.findById(id);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            student.setName(name);
            student.setAge(age);
            studentRepository.save(student);
            logger.debug("Student with id = {} successfully updated", id);
            return true;
        } else {
            logger.warn("Attempted to update non-existent student with id = {}", id);
            return false;
        }
    }

    // Метод для удаления студента по ID
    public boolean deleteStudent(Long id) {
        logger.info("Was invoked method for delete student");
        logger.debug("Trying to delete student with id = {}", id);

        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            logger.debug("Student with id = {} successfully deleted", id);
            return true;
        } else {
            logger.warn("Attempted to delete non-existent student with id = {}", id);
            return false;
        }
    }

    // Метод для получения всех студентов
    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }

    // Метод для поиска студентов по возрасту
    public List<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for get students by age");
        logger.debug("Filtering students by age = {}", age);

        List<Student> filtered = studentRepository.findAll()
                .stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());

        logger.debug("Found {} students with age = {}", filtered.size(), age);
        return filtered;
    }

    // Метод для получения всех студентов, которые в диапазоне по возрасту
    public List<Student> getStudentsByAgeRange(int minAge, int maxAge) {
        logger.info("Was invoked method for get students by age range");
        logger.debug("Fetching students with age between {} and {}", minAge, maxAge);

        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range", students.size());
        return students;
    }

    // Метод для привязки студента к факультету
    public Student assignFacultyToStudent(Long studentId, Long facultyId) {
        logger.info("Was invoked method for assign faculty to student");
        logger.debug("Assigning facultyId = {} to studentId = {}", facultyId, studentId);

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            logger.warn("Student not found with id = {}", studentId);
            return null;
        }

        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        if (faculty == null) {
            logger.warn("Faculty not found with id = {}", facultyId);
            return null;
        }

        student.setFaculty(faculty);
        Student updatedStudent = studentRepository.save(student);
        logger.debug("Faculty assigned to student. Updated student id = {}", updatedStudent.getId());
        return updatedStudent;
    }

    // Метод для получения факультета студента
    public Faculty getStudentFaculty(Long studentId) {
        logger.info("Was invoked method for get student faculty");
        logger.debug("Getting faculty for studentId = {}", studentId);

        return studentRepository.findFacultyById(studentId)
                .orElseThrow(() -> {
                    logger.error("Faculty not found for student ID {}", studentId);
                    return new RuntimeException("Faculty not found for student ID " + studentId);
                });
    }

    // Метод для получения количества всех студентов
    public long getTotalStudents() {
        logger.info("Was invoked method for get total students count");
        long count = studentRepository.countAllStudents();
        logger.debug("Total number of students = {}", count);
        return count;
    }

    // Метод для получения среднего возраста студентов
    public double getAverageAge() {
        logger.info("Was invoked method for get average student age");
        double avg = studentRepository.findAverageAge();
        logger.debug("Average student age = {}", avg);
        return avg;
    }

    // Метод для получения 5 последних студентов
    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        Pageable pageable = (Pageable) PageRequest.of(0, 5);
        List<Student> students = studentRepository.findTop5ByOrderByIdDesc(pageable);
        logger.debug("Retrieved last five students, count = {}", students.size());
        return students;
    }
}
