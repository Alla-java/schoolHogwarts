package ru.hogwarts.school.controllerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import org.springframework.http.HttpEntity;

import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Test
    public void testCreateStudent() {
        Student newStudent = new Student("Иван Иванов", 25);

        ResponseEntity<Student> response = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(newStudent), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Иван Иванов", response.getBody().getName());
        assertEquals(25, response.getBody().getAge());
    }

    @Test
    public void testGetStudent() {
        Student newStudent = new Student("Иван Иванов", 25);
        newStudent = studentRepository.save(newStudent);

        Long studentId = newStudent.getId();

        ResponseEntity<Student> response = restTemplate.exchange(

                "/students/" + studentId, HttpMethod.GET, null, Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(studentId, response.getBody().getId());
    }

    @Test
    public void testGetAllStudents() {
        Student student1 = new Student("Иван Иванов", 20);
        student1 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student1), Student.class).getBody();

        Student student2 = new Student("Петр Петров", 20);
        student2 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student2), Student.class).getBody();

        Student student3 = new Student("Алексей Алексеев", 25);
        student3 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student3), Student.class).getBody();

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "/students", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    public void testUpdateStudent() {
        Student newStudent = new Student("Иван Иванов", 20);
        newStudent = studentRepository.save(newStudent);

        Long studentId = newStudent.getId();

        Student updatedStudent = new Student("Иван Фролов", 26);
        updatedStudent.setId(studentId);


        ResponseEntity<Student> response = restTemplate.exchange(
                "/students/" + studentId, HttpMethod.PUT, new HttpEntity<>(updatedStudent), Student.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Иван Фролов", response.getBody().getName());
        assertEquals(26, response.getBody().getAge());
    }

    @Test
    public void testDeleteStudent() {
        Student newStudent = new Student("Иван Иванов", 20);
        newStudent = studentRepository.save(newStudent);

        Long studentId = newStudent.getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/students/" + studentId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Дополнительно можно проверить, что студента больше нет в системе
        ResponseEntity<Student> getResponse = restTemplate.exchange(
                "/students/" + studentId, HttpMethod.GET, null, Student.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    }

    @Test
    public void testGetStudentsByAge() {
        Student student1 = new Student("Иван Иванов", 20);
        student1 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student1), Student.class).getBody();

        Student student2 = new Student("Петр Петров", 20);
        student2 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student2), Student.class).getBody();

        Student student3 = new Student("Алексей Алексеев", 25);
        student3 = restTemplate.exchange(
                "/student", HttpMethod.POST, new HttpEntity<>(student3), Student.class).getBody();

        // Получаем всех студентов с возрастом 20
        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "/students/age/20", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Student> students = response.getBody();
        assertTrue(students.size() > 1); // Ожидаем, что список содержит больше одного студента с возрастом 20

        // Проверяем, что оба студента с возрастом 20 присутствуют в списке
        assertTrue(students.stream().anyMatch(s -> s.getName().equals("Иван Иванов") && s.getAge() == 20));
        assertTrue(students.stream().anyMatch(s -> s.getName().equals("Петр Петров") && s.getAge() == 20));
    }

    @Test
    public void testGetStudentsByAgeRange() {
        Student student1 = new Student("Иван Иванов", 20);
        student1 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student1), Student.class).getBody();

        Student student2 = new Student("Петр Петров", 20);
        student2 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student2), Student.class).getBody();

        Student student3 = new Student("Алексей Алексеев", 35);
        student3 = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student3), Student.class).getBody();

        int minAge = 20;
        int maxAge = 30;

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "/students/age/range?min=" + minAge + "&max=" + maxAge, HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    public void testAssignFacultyToStudent() {
        Faculty faculty = new Faculty("Гринвич", "зеленый");
        faculty = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class).getBody();

        Student student = new Student("Иван Иванов", 20);
        student = restTemplate.exchange(
                "/students", HttpMethod.POST, new HttpEntity<>(student), Student.class).getBody();

        // Формируем URL для запроса (привязываем студента к факультету)
        String url = UriComponentsBuilder.fromPath("/students/{studentId}/faculty/{facultyId}")
                .buildAndExpand(student.getId(), faculty.getId())
                .toUriString();

        // Отправляем PUT запрос для привязки студента к факультету
        ResponseEntity<Student> response = restTemplate.exchange(
                url, HttpMethod.PUT, null, Student.class);

        // Проверяем, что запрос был успешным и студент был обновлен
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(faculty.getId(), response.getBody().getFaculty().getId());
        assertEquals(student.getId(), response.getBody().getId());
    }

    @Test
    public void testGetStudentFaculty() {

        // Создаем факультет
        Faculty faculty = new Faculty("гриффиндор", "зеленый");

        // Создаем студента
        Student newStudent = new Student("Иван Иванов", 20);
        newStudent.setFaculty(faculty); // Устанавливаем факультет для студента

        faculty = facultyRepository.save(faculty);
        newStudent = studentRepository.save(newStudent);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "/students/" + newStudent.getId() + "/faculty", Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(faculty.getName(), response.getBody().getName());
        assertEquals(faculty.getColor(), response.getBody().getColor());
    }
}
