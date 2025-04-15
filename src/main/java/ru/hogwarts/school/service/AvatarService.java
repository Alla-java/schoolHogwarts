package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


@Service
public class AvatarService{

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

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
        logger.info("Was invoked method for get avatar by ID");
        logger.debug("Getting avatar with id = {}", id);
        Optional<Avatar> avatar = avatarRepository.findById(id);
        if (avatar.isEmpty()) {
            logger.warn("No avatar found with id = {}", id);
        }
        return avatar;
    }

    // Метод для получения аватара по studentId
    @Transactional(readOnly = true)
    public Optional<Avatar> getAvatarByStudentId(Long studentId) {
        logger.info("Was invoked method for get avatar by studentId");
        logger.debug("Getting avatar with studentId = {}", studentId);
        Optional<Avatar> avatar = avatarRepository.findByStudentId(studentId);
        if (avatar.isEmpty()) {
            logger.warn("No avatar found for studentId = {}", studentId);
        }
        return avatar;
    }

    // Метод для сохранения аватара в БД и на диск
    @Transactional
    public Avatar saveAvatar(MultipartFile file, Long studentId) throws IOException {
        logger.info("Was invoked method for save avatar");
        logger.debug("Saving avatar for studentId = {}", studentId);

        try {
            String fileName = studentId + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(avatarsDir, fileName);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            Avatar avatar = new Avatar();
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(file.getSize());
            avatar.setMediaType(file.getContentType());
            avatar.setData(file.getBytes());

            logger.debug("Avatar file saved to path = {}", filePath);

            return avatarRepository.save(avatar);
        } catch (IOException e) {
            logger.error("Failed to save avatar for studentId = {}", studentId, e);
            throw e;
        }
    }

    // Метод для получения аватара из директории
    public FileSystemResource getAvatarFromFileSystem(Long studentId) {
        logger.info("Was invoked method for get avatar from file system");
        String fileName = studentId + "_avatar.jpg";
        Path path = Paths.get(avatarsDir, fileName);
        logger.debug("Checking existence of file at path = {}", path);

        if (Files.exists(path)) {
            logger.debug("Avatar file found for studentId = {}", studentId);
            return new FileSystemResource(path.toFile());
        } else {
            logger.warn("Avatar file not found for studentId = {}", studentId);
            return null;
        }
    }

    // Метод для получения аватаров с пагинацией
    public Page<Avatar> getAvatarsPage(Pageable pageable) {
        logger.info("Was invoked method for get avatars page");
        logger.debug("Fetching avatars page with pageable = {}", pageable);
        return avatarRepository.findAll(pageable);
    }
}