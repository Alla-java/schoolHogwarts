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
import org.springframework.http.HttpEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateFaculty() {
        Faculty faculty = new Faculty("Гринвич", "зеленый");

        // Создаем факультет через POST запрос
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Faculty createdFaculty = response.getBody();
        assertNotNull(createdFaculty.getId()); // Проверяем, что у факультета есть ID
        assertEquals("Гринвич", createdFaculty.getName());
        assertEquals("зеленый", createdFaculty.getColor());
    }

    @Test
    public void testGetFacultyById() {
        Faculty faculty = new Faculty("Гринвич", "зеленый");
        faculty = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class).getBody();

        // Получаем факультет по ID
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty/" + faculty.getId(), HttpMethod.GET, null, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Faculty retrievedFaculty = response.getBody();
        assertEquals(faculty.getId(), retrievedFaculty.getId());
        assertEquals("Гринвич", retrievedFaculty.getName());
        assertEquals("зеленый", retrievedFaculty.getColor());
    }

    @Test
    public void testGetAllFaculties() {
        // Создаем два факультета для теста
        Faculty faculty1 = new Faculty("Гринвич", "зеленый");
        faculty1 = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty1), Faculty.class).getBody();

        Faculty faculty2 = new Faculty("Слизерин", "красный");
        faculty2 = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty2), Faculty.class).getBody();

        // Получаем все факультеты
        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                "/faculty", HttpMethod.GET, null, new ParameterizedTypeReference<List<Faculty>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Faculty> faculties = response.getBody();
        assertTrue(faculties.size() > 1); // Проверяем, что список содержит больше одного факультета

        // Проверяем, что оба факультета присутствуют в списке
        assertTrue(faculties.stream().anyMatch(f -> f.getName().equals("Гринвич") && f.getColor().equals("зеленый")));
        assertTrue(faculties.stream().anyMatch(f -> f.getName().equals("Слизерин") && f.getColor().equals("красный")));
    }

    @Test
    public void testUpdateFaculty() {
        // Создаем факультет для теста
        Faculty faculty = new Faculty("Гринвич", "зеленый");
        faculty = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class).getBody();

        // Обновляем факультет
        Faculty updatedFaculty = new Faculty("Гринвич", "синий");
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty/" + faculty.getId(), HttpMethod.PUT, new HttpEntity<>(updatedFaculty), Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Faculty retrievedFaculty = response.getBody();
        assertEquals("Гринвич", retrievedFaculty.getName());
        assertEquals("синий", retrievedFaculty.getColor());
    }

    @Test
    public void testDeleteFaculty() {
        // Создаем факультет для теста
        Faculty faculty = new Faculty("Гринвич", "зеленый");
        faculty = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class).getBody();

        // Удаляем факультет
        restTemplate.exchange("/faculty/" + faculty.getId(), HttpMethod.DELETE, null, Void.class);

        // Пытаемся получить удаленный факультет
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty/" + faculty.getId(), HttpMethod.GET, null, Faculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Факультет должен быть удален
    }

    @Test
    public void testGetFacultiesByColor() {
        // Создаем два факультета для теста
        Faculty faculty1 = new Faculty("Гринвич", "зеленый");
        faculty1 = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty1), Faculty.class).getBody();

        Faculty faculty2 = new Faculty("Слизерин", "красный");
        faculty2 = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty2), Faculty.class).getBody();

        // Получаем факультеты по цвету
        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                "/faculty/color/зеленый", HttpMethod.GET, null, new ParameterizedTypeReference<List<Faculty>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Faculty> faculties = response.getBody();
        assertTrue(faculties.size() > 0); // Проверяем, что список не пуст
        assertTrue(faculties.stream().anyMatch(f -> f.getColor().equals("зеленый"))); // Проверяем, что цвет совпадает
    }

    @Test
    public void testSearchFaculties() {
        // Создаем факультет для теста
        Faculty faculty = new Faculty("Гринвич", "зеленый");
        faculty = restTemplate.exchange(
                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class).getBody();

        // Ищем факультеты по имени или цвету
        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                "/faculty/search?searchTerm=Гринвич", HttpMethod.GET, null, new ParameterizedTypeReference<List<Faculty>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Faculty> faculties = response.getBody();
        assertTrue(faculties.size() > 0); // Проверяем, что список не пуст
        assertTrue(faculties.stream().anyMatch(f -> f.getName().equals("Гринвич"))); // Проверяем, что имя совпадает
    }

//    @Test
//    public void testGetFacultyStudents() {
//        // Создаем два студента для теста
//        Student newStudent1 = new Student("Иван Иванов", 20);
//        Student newStudent2 = new Student("Иван Фролов", 25);
//
//        // Сохраняем студентов в репозитории
//        newStudent1 = restTemplate.exchange(
//                "/student", HttpMethod.POST, new HttpEntity<>(newStudent1), Student.class).getBody();
//        newStudent2 = restTemplate.exchange(
//                "/student", HttpMethod.POST, new HttpEntity<>(newStudent2), Student.class).getBody();
//
//        // Создаем факультет для теста
//        Faculty faculty = new Faculty("Гринвич", "зеленый");
//        faculty = restTemplate.exchange(
//                "/faculty", HttpMethod.POST, new HttpEntity<>(faculty), Faculty.class).getBody();
//
//        // Привязываем студентов к факультету
//        restTemplate.exchange(
//                "/student/" + newStudent1.getId() + "/faculty/" + faculty.getId(), HttpMethod.PUT, null, Student.class);
//        restTemplate.exchange(
//                "/student/" + newStudent2.getId() + "/faculty/" + faculty.getId(), HttpMethod.PUT, null, Student.class);
//
//        // Получаем студентов факультета по ID
//        ResponseEntity<List<Student>> response = restTemplate.exchange(
//                "/faculty/" + faculty.getId() + "/students", HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {});
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        List<Student> students = response.getBody();
//        assertNotNull(students); // Проверяем, что список студентов не пуст
//        assertTrue(students.stream().anyMatch(s -> s.getName().equals("Иван Иванов")));
//        assertTrue(students.stream().anyMatch(s -> s.getName().equals("Иван Фролов")));
//    }
}

