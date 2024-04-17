package fr.norsys.gedapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.norsys.gedapi.dto.DocumentDto;
import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.service.DocumentService;
import fr.norsys.gedapi.service.NextcloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class DocumentController {

    private final NextcloudService nextcloudService;
    private final DocumentService documentService;

    public DocumentController(NextcloudService nextcloudService, DocumentService documentService) {
        this.nextcloudService = nextcloudService;
        this.documentService = documentService;
    }

    private static final Logger logger = LoggerFactory.getLogger(NextcloudService.class);

    @PutMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            nextcloudService.uploadFile(file.getBytes(), file.getOriginalFilename());
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);

        } catch (IOException e) {
            logger.error("Error uploading file " + file.getOriginalFilename(), e);
            return new ResponseEntity<>("Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] fileContent = nextcloudService.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

//    @PutMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Document> createDocument(@RequestPart("file") MultipartFile file,  DocumentDto documentDto) {
//        try {
//            Document document = new Document(
//                    documentDto.getId(),
//                    documentDto.getName(),
//                    documentDto.isFolder(),
//                    documentDto.getCreationDate(),
//                    documentDto.getMetadata(),
//                    documentDto.getFilePath()
//            );
//            Document createdDocument = documentService.createDocument(document, file.getBytes());
//            return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
//        } catch (IOException e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> createDocument(
            @RequestParam("file") MultipartFile file,DocumentDto documentDto) {
        try {
            Document createdDocument = documentService.createDocument(documentDto, file);
            return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @GetMapping("/{id}")
//    public ResponseEntity<Document> getDocument(@PathVariable UUID id) {
//        Document document = documentService.getDocument(id);
//        return new ResponseEntity<>(document, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
//        documentService.deleteDocument(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
}