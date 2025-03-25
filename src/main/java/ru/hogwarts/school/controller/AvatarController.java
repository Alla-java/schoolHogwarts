package ru.hogwarts.school.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;

import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController (AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    //Эндпоинт для загрузки картинки и в БД и на локальный диск
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("studentId") Long studentId) {
        try {
            Avatar savedAvatar = avatarService.saveAvatar(file, studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAvatar);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Эндопоинт для получения картинки из БД
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getAvatarFromDatabase(@PathVariable Long id) {
        Optional<Avatar> avatar = avatarService.getAvatarById(id);
        if (avatar.isPresent()) {
            return ResponseEntity.ok()
                    .header("Content-Type", avatar.get().getMediaType())
                    .body(avatar.get().getData());
        }
        return ResponseEntity.notFound().build();
    }

    //Эндпоинт для получения картинки из файловой системы
    @GetMapping("/file/{studentId}")
    public ResponseEntity<FileSystemResource> getAvatarFromFileSystem(@PathVariable Long studentId) {
        FileSystemResource resource = avatarService.getAvatarFromFileSystem(studentId);
        if (resource != null) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
}
