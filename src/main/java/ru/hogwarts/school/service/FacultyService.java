package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    // Метод для создания нового факультета
    public Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty(name, color);
        return facultyRepository.save(faculty);
    }

    // Метод для получения факультета по ID
    public Faculty getFaculty(Long id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        return faculty.orElse(null); // Возвращаем null, если факультет не найден
    }

    // Метод для обновления информации о факультете
    public boolean updateFaculty(Long id, String name, String color) {
        Optional<Faculty> facultyOptional = facultyRepository.findById(id);
        if (facultyOptional.isPresent()) {
            Faculty faculty = facultyOptional.get();
            faculty.setName(name);
            faculty.setColor(color);
            facultyRepository.save(faculty); // Сохраняем обновленный факультет
            return true;
        }
        return false;
    }

    // Метод для удаления факультета по ID
    public boolean deleteFaculty(Long id) {
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id); // Удаляем факультет из репозитория
            return true;
        }
        return false;
    }

    // Метод для получения всех факультетов
    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll(); // Получаем все факультеты из репозитория
    }

    // Метод для поиска факультетов по цвету
    public List<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findAll()
                .stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }
}
