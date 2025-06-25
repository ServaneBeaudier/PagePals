package com.pagepals.user.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.pagepals.user.dto.UpdateUserProfileDTO;
import com.pagepals.user.dto.UserProfileCreateRequest;
import com.pagepals.user.model.UserProfile;
import com.pagepals.user.repository.UserProfileRepository;
import com.pagepals.user.service.FileStorageService;
import com.pagepals.user.service.UserProfileService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    private final FileStorageService fileStorageService;

    private final UserProfileRepository userProfileRepository;

    @PostMapping("/create")
    public ResponseEntity<Void> createUserProfile(@RequestBody UserProfileCreateRequest request) {
        userProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUserProfile(@RequestBody UpdateUserProfileDTO dto) {
        userProfileService.updateProfile(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file,
            @RequestParam("userId") long userId) {
        // Vérifie que le fichier n'est pas vide
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide.");
        }

        // Enregistre le fichier et récupère son nom
        String fileName = fileStorageService.storeFile(file);

        // Met à jour le profil utilisateur
        userProfileService.updatePhoto(userId, fileName);

        return ResponseEntity.ok("Photo de profil mise à jour : " + fileName);
    }

    @DeleteMapping("/photo")
    public ResponseEntity<String> deletePhoto(@RequestParam("userId") Long userId) {
        // Récupérer l'utilisateur
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Vérifier si une photo est associée
        if (user.getPhotoProfil() != null) {
            // Supprimer le fichier physique
            fileStorageService.deleteFile(user.getPhotoProfil());

            // Nettoyer la BDD
            user.setPhotoProfil(null);
            userProfileRepository.save(user);

            return ResponseEntity.ok("Photo de profil supprimée.");
        } else {
            return ResponseEntity.badRequest().body("Aucune photo à supprimer.");
        }
    }

    @GetMapping("/photo/{fileName}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String fileName) {
        try {
            Resource file = fileStorageService.loadFile(fileName);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG) // adapte si PNG ou autre
                    .body(file);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
