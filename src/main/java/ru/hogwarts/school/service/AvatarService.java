package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Service
public class AvatarService{
    private final AvatarRepository avatarRepository;


    @Value("${avatars.dir.path}")
    private String avatarsDir;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    // Метод для получения аватара по ID
    @Transactional(readOnly = true)
    public Optional<Avatar> getAvatarById(Long id) {
        return avatarRepository.findById(id);
    }

    // Метод для получения аватара по studentId
    @Transactional(readOnly = true)
    public Optional<Avatar> getAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    // Метод для сохранения аватара в БД и на диск
    @Transactional
    public Avatar saveAvatar(MultipartFile file, Long studentId) throws IOException {
        // Генерируем уникальное имя для файла
        String fileName = studentId + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(avatarsDir, fileName);

        // Сохраняем файл на диск
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // Создаем объект Avatar и сохраняем в БД
        Avatar avatar = new Avatar();
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        return avatarRepository.save(avatar);
    }

    // Метод для получения аватара из директории
    public FileSystemResource getAvatarFromFileSystem(Long studentId) {
        String fileName = studentId + "_avatar.jpg"; // Можно изменить на более универсальный формат
        Path path = Paths.get(avatarsDir, fileName);
        if (Files.exists(path)) {
            return new FileSystemResource(path.toFile());
        }
        return null;
    }
}
