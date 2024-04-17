package fr.norsys.gedapi.service;

import fr.norsys.gedapi.dao.DocumentDao;
import fr.norsys.gedapi.dao.MetadataDao;
import fr.norsys.gedapi.dto.DocumentDto;
import fr.norsys.gedapi.dto.MetadataDto;
import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            String filePath = nextcloudService.uploadFile(fileData,file.getOriginalFilename());
            Document document = Document.builder()
                    .name(file.getOriginalFilename())
                    .isFolder(false)
                    .creationDate(LocalDateTime.now())
                    .filePath(filePath)
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

    public Document getDocument(int id) {
        return documentDao.getDocumentById(id);
    }

    public void deleteDocument(int id) {
        Document document = getDocument(id);
        nextcloudService.deleteFile(document.getName());
        documentDao.deleteDocument(id);
    }

}