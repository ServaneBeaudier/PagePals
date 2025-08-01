package com.pagepals.user.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.pagepals.user.dto.*;
import com.pagepals.user.exception.UserNotFoundException;
import com.pagepals.user.model.UserProfile;
import com.pagepals.user.repository.UserProfileRepository;
import com.pagepals.user.service.FileStorageService;
import com.pagepals.user.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    private final FileStorageService fileStorageService;

    private final UserProfileRepository userProfileRepository;

    @PostMapping("/create")
    public ResponseEntity<Void> createUserProfile(@RequestBody UserProfileCreateRequest request) {
        try {
            System.out.println("==> Requête reçue dans /api/user/create : " + request);
            userProfileService.createProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            System.out.println("❌ Erreur pendant la création de l'utilisateur :");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUserProfile(@RequestBody UpdateUserProfileDTO dto) {
        userProfileService.updateProfile(dto);
        System.out.println("DTO reçu : " + dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update/photo")
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
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

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

            String contentType = null;
            try {
                Path path = file.getFile().toPath();
                contentType = Files.probeContentType(path);
            } catch (IOException ex) {
                // log warning ou fallback silencieux
            }

            if (contentType == null) {
                if (fileName.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else {
                    contentType = "application/octet-stream";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pseudos")
    public List<ParticipantDTO> getPseudosByIds(@RequestParam("ids") List<Long> userIds) {
        return userProfileRepository.findAllById(userIds).stream()
                .map(user -> new ParticipantDTO(user.getId(), user.getPseudo(), user.getPhotoProfil()))
                .collect(Collectors.toList());
    }

    @GetMapping("/infos")
    public UserInfoDTO getUserInfo(@RequestParam("id") long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        return new UserInfoDTO(user.getId(), user.getPseudo(), user.getPhotoProfil(), user.getDateInscription(),
                user.getBio());
    }

    @GetMapping("/message")
    public List<MessageDTO> getInfosPourMessage(@RequestParam("ids") List<Long> userIds) {
        return userProfileRepository.findAllById(userIds).stream()
                .map(user -> MessageDTO.builder()
                        .auteurId(user.getId())
                        .pseudoAuteur(user.getPseudo())
                        .photoAuteur(user.getPhotoProfil())
                        .build())
                .collect(Collectors.toList());
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupCompte(@RequestParam("userId") Long userId) {
        try {
            userProfileService.cleanupUser(userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
        }
    }
}
