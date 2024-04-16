package fr.norsys.gedapi.service;

import fr.norsys.gedapi.dao.DocumentDao;
import fr.norsys.gedapi.model.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentDao documentDao;
    private final NextcloudService nextcloudService;

    public DocumentService(DocumentDao documentDao, NextcloudService nextcloudService) {
        this.documentDao = documentDao;
        this.nextcloudService = nextcloudService;
    }

    @Transactional
    public Document createDocument(Document document, byte[] fileData) {
        nextcloudService.uploadFile(fileData, document.getName());
        return documentDao.save(document);
    }

    public Document getDocument(UUID id) {
        return documentDao.getDocumentById(id);
    }

    public void deleteDocument(UUID id) {
        Document document = getDocument(id);
        nextcloudService.deleteFile(document.getName());
        documentDao.deleteDocument(id);
    }

}