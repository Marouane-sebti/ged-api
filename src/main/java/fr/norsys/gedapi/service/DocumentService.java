package fr.norsys.gedapi.service;

import fr.norsys.gedapi.dao.DocumentDao;
import fr.norsys.gedapi.dao.MetadataDao;
import fr.norsys.gedapi.dto.MetadataDto;
import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentDao documentDao;
    private final MetadataDao metadataDao;
    private final NextcloudService nextcloudService;


    public DocumentService(DocumentDao documentDao, MetadataDao metadataDao, NextcloudService nextcloudService) {
        this.documentDao = documentDao;
        this.metadataDao = metadataDao;
        this.nextcloudService = nextcloudService;
    }

    @Transactional
    public Document createDocument(List<MetadataDto> metadataDto, MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        String filePath = nextcloudService.uploadFile(fileData, file.getOriginalFilename());
        String fileHash = calculateSHA256(fileData);
        System.out.println("File hash: " + fileHash);
        Document document = Document.builder()
                .name(file.getOriginalFilename())
                .isFolder(false)
                .creationDate(LocalDateTime.now())
                .filePath(filePath)
                .hashValue(fileHash)
                .build();
        Document savedDocument = documentDao.save(document);

        System.out.println("Saved document id: " + savedDocument.getId());

        if (metadataDto != null) {
            List<Metadata> metadataList = metadataDto.stream()
                    .map(metadata -> new Metadata(savedDocument.getId(), metadata.getKey(), metadata.getValue()))
                    .collect(Collectors.toList());
            metadataDao.saveAll(metadataList);
            savedDocument.setMetadata(metadataList);
        }
        return savedDocument;
    }

    private String calculateSHA256(byte[] fileData) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileData);
            return bytesToHex(hashBytes); // Convert hash bytes to hexadecimal string
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
            return null; // Return null or throw exception based on your error handling strategy
        }
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

//    public Document getDocument(UUID id) {
//        return documentDao.getDocumentById(id);
//    }

//    public void deleteDocument(UUID id) {
//        Document document = getDocument(id);
//        nextcloudService.deleteFile(document.getName());
//        documentDao.deleteDocument(id);
//    }

}