package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.Map;

public class StudentService {

    private Map<Long, Student> students;
    private long idCounter;

    // Конструктор
    public StudentService() {
        this.students = new HashMap<>();
        this.idCounter = 1; // Начальный идентификатор
    }

    // Метод для создания нового студента
    public Student createStudent(String name, int age) {
        Student student = new Student(idCounter, name, age);
        students.put(idCounter, student);
        idCounter++; // Увеличиваем счетчик ID для следующего студента
        return student;
    }

    // Метод для получения студента по ID
    public Student getStudent(Long id) {
        return students.get(id);
    }

    // Метод для обновления информации о студенте
    public boolean updateStudent(Long id, String name, int age) {
        Student student = students.get(id);
        if (student != null) {
            student.setName(name);
            student.setAge(age);
            return true;
        }
        return false;
    }

    // Метод для удаления студента по ID
    public boolean deleteStudent(Long id) {
        if (students.containsKey(id)) {
            students.remove(id);
            return true;
        }
        return false;
    }

    // Вспомогательный метод для получения всех студентов
    public Map<Long, Student> getAllStudents() {
        return students;
    }
}
