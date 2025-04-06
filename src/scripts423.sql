-- Первый JOIN , чтобы получить информацию обо всех студентах
SELECT s.name AS student_name, s.age, f.name AS faculty_name
FROM student s
INNER JOIN faculty f ON s.faculty_id = f.id;

-- Второй JOIN, чтобы получить только тех студентов, у которых есть аватарки
SELECT s.name AS student_name, s.age, a.filePath
FROM student s
INNER JOIN avatar a ON s.id = a.student_id;