package ru.hogwarts.school.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWithMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FacultyService facultyService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    // Тест для создания студента
    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student("Иван", 20);
        when(studentService.createStudent("Иван", 20)).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.age").value(20));

        verify(studentService, times(1)).createStudent("Иван", 20);
    }

    // Тест для получения студента по ID
    @Test
    public void testGetStudent() throws Exception {
        Student student = new Student("Иван", 20);
        student.setId(1L);
        when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.age").value(20));

        verify(studentService, times(1)).getStudent(1L);
    }

    @Test
    public void testGetAllStudents() throws Exception {
        Student student1 = new Student("Иван", 20);
        student1.setId(1L);
        Student student2 = new Student("Мария", 22);
        student2.setId(2L);
        Student student3 = new Student("Петр", 24);
        student3.setId(3L);

        // Возвращаем список студентов из mock-сервиса
        when(studentService.getAllStudents()).thenReturn(List.of(student1, student2, student3));

        // Выполнение GET-запроса для получения всех студентов
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))  // Проверяем, что в ответе 3 студента
                .andExpect(jsonPath("$[0].name").value("Иван"))   // Проверяем первого студента
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].name").value("Мария"))  // Проверяем второго студента
                .andExpect(jsonPath("$[1].age").value(22))
                .andExpect(jsonPath("$[2].name").value("Петр"))   // Проверяем третьего студента
                .andExpect(jsonPath("$[2].age").value(24));

        // Проверяем, что метод getAllStudents был вызван один раз
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    public void testUpdateStudent() throws Exception {
        Student updatedStudent = new Student("Иван Федоров", 22);
        updatedStudent.setId(1L);
        when(studentService.updateStudent(1L, "Иван Федоров", 22)).thenReturn(true);
        when(studentService.getStudent(1L)).thenReturn(updatedStudent);

        mockMvc.perform(put("/student/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Федоров"))
                .andExpect(jsonPath("$.age").value(22));

        verify(studentService, times(1)).updateStudent(1L, "Иван Федоров", 22);
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Student student = new Student("Иван", 20);
        student.setId(1L);

        when(studentService.deleteStudent(1L)).thenReturn(true);

        // Выполнение запроса на удаление студента по ID
        mockMvc.perform(delete("/student/{id}", 1L))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    public void testGetStudentsByAge() throws Exception {
        Student student1 = new Student("Иван", 20);
        student1.setId(1L);
        Student student2 = new Student("Мария", 20);
        student2.setId(2L);
        Student student3 = new Student("Петр", 21);
        student3.setId(3L);

        // Возвращаем список студентов с возрастом 20
        when(studentService.getStudentsByAge(20)).thenReturn(List.of(student1, student2));

        // Выполнение GET-запроса для получения студентов по возрасту
        mockMvc.perform(get("/student/age/{age}", 20))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Проверяем, что в ответе 2 студента с возрастом 20
                .andExpect(jsonPath("$[0].name").value("Иван"))   // Проверяем первого студента
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].name").value("Мария"))  // Проверяем второго студента
                .andExpect(jsonPath("$[1].age").value(20));

        // Проверяем, что метод getStudentsByAge был вызван один раз
        verify(studentService, times(1)).getStudentsByAge(20);
    }

    @Test
    public void testGetStudentsByAgeRange() throws Exception {
        // Создание нескольких студентов с разным возрастом
        Student student1 = new Student("Федор Федоров", 20);
        student1.setId(1L);
        Student student2 = new Student("Мария Иванова", 22);
        student2.setId(2L);
        Student student3 = new Student("Александр Петров", 24);
        student3.setId(3L);
        Student student4 = new Student("Петр Смирнов", 26);
        student4.setId(4L);

        // Возвращаем список студентов, чей возраст в пределах от 20 до 25 лет
        when(studentService.getStudentsByAgeRange(20, 25)).thenReturn(List.of(student1, student2, student3));

        // Выполнение GET-запроса для получения студентов по возрастному диапазону
        mockMvc.perform(get("/student/age/range")
                        .param("min", "20")
                        .param("max", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))  // Проверяем, что в ответе 3 студента
                .andExpect(jsonPath("$[0].name").value("Федор Федоров"))  // Проверяем первого студента
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].name").value("Мария Иванова"))  // Проверяем второго студента
                .andExpect(jsonPath("$[1].age").value(22))
                .andExpect(jsonPath("$[2].name").value("Александр Петров"))  // Проверяем третьего студента
                .andExpect(jsonPath("$[2].age").value(24));

        // Проверяем, что метод getStudentsByAgeRange был вызван один раз с параметрами min = 20 и max = 25
        verify(studentService, times(1)).getStudentsByAgeRange(20, 25);
    }

    @Test
    public void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty("Гринвич", "желтый");
        when(studentService.getStudentFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/{id}/faculty", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гринвич"));

        verify(studentService, times(1)).getStudentFaculty(1L);
    }
}
