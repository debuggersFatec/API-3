package com.api_3.api_3.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api_3.api_3.model.embedded.FileAttachment;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    // Lista de tipos MIME permitidos para imagem
    private final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", 
        "image/png", 
        "image/jpg", 
        "image/webp"
    );

    public FileStorageService() {
        // Define a pasta de upload como "uploads" no diretório atual de execução
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            // Cria o diretório se não existir
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório 'uploads' para armazenar os arquivos.", ex);
        }
    }

    // Novo método para validar se o arquivo é uma imagem
    public void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new RuntimeException("Formato de arquivo inválido. Apenas imagens (JPEG, PNG, WEBP) são permitidas.");
        }
    }

    public FileAttachment storeFile(MultipartFile file, String uploaderUuid) {
        // Normaliza o nome do arquivo original
        String originalFileName = file.getOriginalFilename();
        
        // Extrai a extensão (ex: .pdf, .png)
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Gera um "nome criptografado" (UUID) para salvar no disco com segurança
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Define o caminho de destino
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            
            // Copia o arquivo para o destino (substituindo se existir um com mesmo UUID, o que é raríssimo)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retorna o objeto com os metadados solicitados
            return new FileAttachment(originalFileName, storedFileName, uploaderUuid, new Date());
            
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível armazenar o arquivo " + originalFileName + ". Tente novamente!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Arquivo não encontrado: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Arquivo não encontrado: " + fileName, ex);
        }
    }
}