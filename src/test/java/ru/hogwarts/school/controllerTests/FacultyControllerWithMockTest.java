package ru.hogwarts.school.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import ru.hogwarts.school.model.Student;


import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWithMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    // Тест для создания нового факультета
    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty("Гринвич", "красный");
        when(facultyService.createFaculty("Гринвич", "красный")).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гринвич"))
                .andExpect(jsonPath("$.color").value("красный"));

        verify(facultyService, times(1)).createFaculty("Гринвич", "красный");
    }

    @Test
    public void testGetFaculty() throws Exception {
        Faculty faculty = new Faculty("Гринвич", "красный");
        faculty.setId(1L);
        when(facultyService.getFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гринвич"))
                .andExpect(jsonPath("$.color").value("красный"));

        verify(facultyService, times(1)).getFaculty(1L);
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        Faculty faculty1 = new Faculty("Гринвич", "красный");
        Faculty faculty2 = new Faculty("Слизерин", "синий");
        when(facultyService.getAllFaculties()).thenReturn(List.of(faculty1, faculty2));

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Гринвич"))
                .andExpect(jsonPath("$[1].name").value("Слизерин"));

        verify(facultyService, times(1)).getAllFaculties();
    }

    @Test
    public void testUpdateFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty("Гринвич", "красный");
        updatedFaculty.setId(1L);
        when(facultyService.updateFaculty(1L, "Гринвич", "красный")).thenReturn(true);
        when(facultyService.getFaculty(1L)).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculty/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Гринвич"))
                .andExpect(jsonPath("$.color").value("красный"));

        verify(facultyService, times(1)).updateFaculty(1L, "Гринвич", "красный");
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Faculty faculty = new Faculty("Гринвич", "красный");
        faculty.setId(1L);

        when(facultyService.deleteFaculty(1L)).thenReturn(true);

        // Выполнение запроса на удаление факультета по ID
        mockMvc.perform(delete("/faculty/{id}", 1L))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(1L);
    }

    @Test
    public void testGetFacultiesByColor() throws Exception {
        Faculty faculty1 = new Faculty("Гринвич", "красный");
        Faculty faculty2 = new Faculty("Гриффиндор", "красный");
        Faculty faculty3 = new Faculty("Слизерин", "синий");

        // Мокаем поведение для возврата списка факультетов с определенным цветом
        when(facultyService.getFacultiesByColor("красный")).thenReturn(List.of(faculty1, faculty2));

        // Выполнение запроса на получение факультетов по цвету
        mockMvc.perform(get("/faculty/color/{color}", "красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Проверка, что вернулось 2 факультета
                .andExpect(jsonPath("$[0].name").value("Гринвич"))
                .andExpect(jsonPath("$[0].color").value("красный"))
                .andExpect(jsonPath("$[1].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[1].color").value("красный"));

        // Проверка, что метод getFacultiesByColor был вызван один раз с параметром "красный"
        verify(facultyService, times(1)).getFacultiesByColor("красный");
    }

    @Test
    public void testSearchFaculties() throws Exception {
        Faculty faculty = new Faculty("Гринвич", "красный");
        when(facultyService.searchFacultiesByNameOrColor("грин")).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/search")
                        .param("searchTerm", "грин"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Гринвич"))
                .andExpect(jsonPath("$[0].color").value("красный"));

        verify(facultyService, times(1)).searchFacultiesByNameOrColor("грин");
    }

    @Test
    public void testGetFacultyStudents() throws Exception {
        Faculty faculty = new Faculty("Гринвич", "красный");
        Student student1 = new Student("Иван", 20);
        student1.setFaculty(faculty);
        Student student2 = new Student("Мария", 22);
        student2.setFaculty(faculty);
        Student student3 = new Student("Алексей", 21);
        student3.setFaculty(faculty);

        List<Student> students = List.of(student1, student2, student3);

        // Мокаем поведение для возврата списка студентов факультета
        when(facultyService.getFacultyStudents(1L)).thenReturn(students);

        // Выполнение запроса на получение студентов факультета по ID факультета
        mockMvc.perform(get("/faculty/{id}/students", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))  // Проверяем, что вернулся список из 3 студентов
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[0].faculty.name").value("Гринвич"))
                .andExpect(jsonPath("$[1].name").value("Мария"))
                .andExpect(jsonPath("$[1].faculty.name").value("Гринвич"))
                .andExpect(jsonPath("$[2].name").value("Алексей"))
                .andExpect(jsonPath("$[2].faculty.name").value("Гринвич"));

        // Проверка, что метод getFacultyStudents был вызван один раз с параметром 1L
        verify(facultyService, times(1)).getFacultyStudents(1L);
    }
}
