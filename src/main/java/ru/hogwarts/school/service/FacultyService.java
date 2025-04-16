package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    // Метод для создания нового факультета
    public Faculty createFaculty(String name, String color) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty with name = {}, color = {}", name, color);

        Faculty faculty = new Faculty(name, color);
        Faculty saved = facultyRepository.save(faculty);
        logger.debug("Faculty created with id = {}", saved.getId());
        return saved;
    }


    // Метод для получения факультета по ID
    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for get faculty by id");
        logger.debug("Fetching faculty with id = {}", id);

        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isEmpty()) {
            logger.warn("No faculty found with id = {}", id);
        }
        return faculty.orElse(null);
    }

    // Метод для обновления информации о факультете
    public boolean updateFaculty(Long id, String name, String color) {
        logger.info("Was invoked method for update faculty");
        logger.debug("Updating faculty with id = {}, new name = {}, new color = {}", id, name, color);

        Optional<Faculty> facultyOptional = facultyRepository.findById(id);
        if (facultyOptional.isPresent()) {
            Faculty faculty = facultyOptional.get();
            faculty.setName(name);
            faculty.setColor(color);
            facultyRepository.save(faculty);
            logger.debug("Faculty with id = {} successfully updated", id);
            return true;
        } else {
            logger.warn("Attempted to update non-existent faculty with id = {}", id);
            return false;
        }
    }

    // Метод для удаления факультета по ID
    public boolean deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty");
        logger.debug("Trying to delete faculty with id = {}", id);

        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
            logger.debug("Faculty with id = {} successfully deleted", id);
            return true;
        } else {
            logger.warn("Attempted to delete non-existent faculty with id = {}", id);
            return false;
        }
    }

    // Метод для получения всех факультетов
    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        List<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    // Метод для поиска факультетов по цвету
    public List<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for get faculties by color");
        logger.debug("Filtering faculties by color = {}", color);

        List<Faculty> filtered = facultyRepository.findAll()
                .stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());

        logger.debug("Found {} faculties with color = {}", filtered.size(), color);
        return filtered;
    }

    // Метод для поиска факультетов по имени или цвету с игнорированием регистра
    public List<Faculty> searchFacultiesByNameOrColor(String searchTerm) {
        logger.info("Was invoked method for search faculties by name or color");
        logger.debug("Searching faculties with term = {}", searchTerm);

        List<Faculty> results = facultyRepository.findByNameIgnoreCaseContainingOrColorIgnoreCaseContaining(searchTerm, searchTerm);
        logger.debug("Found {} faculties matching search term = {}", results.size(), searchTerm);
        return results;
    }

    // Метод для получения студентов факультета
    public List<Student> getFacultyStudents(Long facultyId) {
        logger.info("Was invoked method for get students of faculty");
        logger.debug("Fetching students for facultyId = {}", facultyId);

        List<Student> students = facultyRepository.findStudentsById(facultyId);
        logger.debug("Found {} students for facultyId = {}", students.size(), facultyId);
        return students;
    }

    // Метод для получения самого длинного названия факультета
    public String getLongestFacultyName() {
        List<Faculty> faculties = facultyRepository.findAll();

        return faculties.stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }
}
