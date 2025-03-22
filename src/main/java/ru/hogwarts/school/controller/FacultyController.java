package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    // Конструктор для внедрения зависимости FacultyService
    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    // Эндпоинт для создания нового факультета
    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty.getName(), faculty.getColor());
    }

    // Эндпоинт для получения факультета по ID
    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    // Эндпоинт для получения всех факультетов
    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    // Эндпоинт для обновления информации о факультете
    @PutMapping("/{id}")
    public Faculty updateFaculty(
            @PathVariable Long id, @RequestBody Faculty updatedFaculty) {
        if (facultyService.updateFaculty(id, updatedFaculty.getName(), updatedFaculty.getColor())) {
            return facultyService.getFaculty(id);
        }
        return null;  // Возвращаем null, Spring автоматически вернёт 404 если факультет не найден
    }

    // Эндпоинт для удаления факультета по ID
    @DeleteMapping("/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    // Эндпоинт для фильтрации факультетов по цвету
    @GetMapping("/color/{color}")
    public List<Faculty> getFacultiesByColor(@PathVariable String color) {
        return facultyService.getFacultiesByColor(color);
    }

    // Эндпоинт для поиска факультета по имени или цвету, игнорируя регистр
    @GetMapping("/search")
    public List<Faculty> searchFaculties(@RequestParam String searchTerm) {
        return facultyService.searchFacultiesByNameOrColor(searchTerm);
    }

    //Эндпоинт для привязки студентов к факультету
    @PostMapping("/{id}")
    public void addStudentsInFaculty () {

    }


    // Эндпоинт для получения студентов факультета по ID факультета
    @GetMapping("/{id}/students")
    public List<Student> getFacultyStudents(@PathVariable Long id) {
        return facultyService.getFacultyStudents(id);
    }
}
