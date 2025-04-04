package ru.hogwarts.school.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Avatar;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    //Метод для поиска аватара по идентификатору студента
    Optional<Avatar> findByStudentId(Long studentId);

    //Метод для получения аватара по ID
    Optional<Avatar> findById(Long id);

    ///

}
