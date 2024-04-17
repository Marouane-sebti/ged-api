package fr.norsys.gedapi.service;

import fr.norsys.gedapi.dao.DocumentDao;
import fr.norsys.gedapi.dto.DocumentDto;
import fr.norsys.gedapi.model.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


@Service
public class DocumentService {

    private final DocumentDao documentDao;
    private final NextcloudService nextcloudService;

    public DocumentService(DocumentDao documentDao, NextcloudService nextcloudService) {
        this.documentDao = documentDao;
        this.nextcloudService = nextcloudService;
    }

    @Transactional
    public Document createDocument(DocumentDto documentDto, MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        String filePath = nextcloudService.uploadFile(fileData,file.getOriginalFilename());
        Document document = Document.builder()
                .name(file.getOriginalFilename())
                .isFolder(false)
                .creationDate(LocalDateTime.now())
                .metadata(documentDto.getMetadata())
                .filePath(filePath)
                .build();
        return documentDao.save(document);
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