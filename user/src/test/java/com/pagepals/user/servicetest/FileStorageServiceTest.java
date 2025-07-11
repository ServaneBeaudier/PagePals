package com.pagepals.user.servicetest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.pagepals.user.service.FileStorageService;

class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurer uploadDir à un chemin temporaire
        fileStorageService.uploadDir = "target/test-uploads";
    }

    @AfterEach
    void tearDown() throws IOException {
        // Nettoyer le dossier test
        Path uploadPath = Paths.get(fileStorageService.uploadDir);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }

    @Test
    void storeFile_shouldSaveFileAndReturnFilename() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Contenu".getBytes());

        String fileName = fileStorageService.storeFile(file);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith("_test.txt"));

        Path savedFile = Paths.get(fileStorageService.uploadDir).resolve(fileName);
        assertTrue(Files.exists(savedFile));
    }

    @Test
    void storeFile_emptyFile_shouldThrow() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileStorageService.storeFile(emptyFile));
        assertEquals("Le fichier est vide.", ex.getMessage());
    }

    @Test
    void loadFile_existingFile_shouldReturnResource() throws IOException {
        // Préparer un fichier sur disque
        Path uploadPath = Paths.get(fileStorageService.uploadDir);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve("file.txt");
        Files.writeString(filePath, "data");

        Resource resource = fileStorageService.loadFile("file.txt");

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void loadFile_notExistingFile_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            fileStorageService.loadFile("inexistant.txt");
        });

        System.out.println("Exception message: " + ex.getMessage());

        assertTrue(ex.getMessage().contains("Erreur lors du chargement du fichier"));
    }

    @Test
    void deleteFile_existingFile_shouldDelete() throws IOException {
        Path uploadPath = Paths.get(fileStorageService.uploadDir);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve("toDelete.txt");
        Files.writeString(filePath, "data");

        fileStorageService.deleteFile("toDelete.txt");

        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_nonExistingFile_shouldNotThrow() {
        assertDoesNotThrow(() -> fileStorageService.deleteFile("fichierInexistant.txt"));
    }

}
