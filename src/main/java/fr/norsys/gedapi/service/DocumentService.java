package fr.norsys.gedapi.service;

import fr.norsys.gedapi.dao.DocumentDao;
import fr.norsys.gedapi.dao.MetadataDao;
import fr.norsys.gedapi.dto.MetadataDto;
import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.DocumentSearchCriteria;
import fr.norsys.gedapi.model.Metadata;
import org.springframework.core.io.ByteArrayResource;
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
    public Document createDocument(List<MetadataDto> metadataDto, MultipartFile file,int userId) throws IOException {
        byte[] fileData = file.getBytes();
        String filePath = nextcloudService.uploadFile(fileData, file.getOriginalFilename());
        String fileHash = calculateSHA256(fileData);
        if(documentDao.getByHash(fileHash)!=null){
            return null;
        }
        Document document = Document.builder()
                .name(file.getOriginalFilename())
                .isFolder(false)
                .creationDate(LocalDateTime.now())
                .filePath(filePath)
                .hashValue(fileHash)
                .size(file.getSize())
                .type(file.getContentType())
                .userId(userId)
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
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public Document getDocument(int id) {
        return documentDao.getDocumentById(id);
    }

    public void deleteDocument(int id) {
        Document document = getDocument(id);
        nextcloudService.deleteFile(document.getName());
        documentDao.deleteDocument(id);
    }

    public List<Document> getAllDocuments() {
        return documentDao.getAllDocuments();
    }

    public List<Document> searchDocuments(DocumentSearchCriteria criteria) {
        return documentDao.searchDocuments(criteria);
    }
    public ByteArrayResource downloadDocument(int documentId) {
        Document document = getDocument(documentId);
        byte[] data = nextcloudService.downloadFile(document.getFilePath());
        return new ByteArrayResource(data);
    }
    public List<Document> getDocumentsByUserId(int userId) {
        return documentDao.getDocumentsByUserId(userId);
    }
}