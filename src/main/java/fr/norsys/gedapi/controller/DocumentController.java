package fr.norsys.gedapi.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.norsys.gedapi.dto.MetadataDto;
import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.DocumentSearchCriteria;
import fr.norsys.gedapi.service.DocumentService;
import fr.norsys.gedapi.service.NextcloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final NextcloudService nextcloudService;
    private final DocumentService documentService;

    private final ObjectMapper objectMapper;

    public DocumentController(NextcloudService nextcloudService, DocumentService documentService, ObjectMapper objectMapper) {
        this.nextcloudService = nextcloudService;
        this.documentService = documentService;
        this.objectMapper = objectMapper;
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
    @GetMapping("/download/name/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] fileContent = nextcloudService.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    //@PutMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PutMapping(value = "/create/{id}")
    public ResponseEntity<Document> createDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam("metadata") String metadataDtoJson
            ,@PathVariable int id) {
        try {
            List<MetadataDto> metadataDtoList = objectMapper.readValue(metadataDtoJson, new TypeReference<List<MetadataDto>>() {
            });
            Document createdDocument = documentService.createDocument(metadataDtoList, file,id);
            return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/{userId}")
    public ResponseEntity<Document> getDocument(@PathVariable int id, @PathVariable int userId) {
        Document document = documentService.getDocumentById(id, userId);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @DeleteMapping("/{idDocument}/{idUser}")
    public ResponseEntity<Void> deleteDocument(@PathVariable int idDocument,@PathVariable int idUser) {
        documentService.deleteDocument(idDocument,idUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PostMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(@RequestBody DocumentSearchCriteria criteria){
        List<Document> documents = documentService.searchDocuments(criteria);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }


    @GetMapping("/download/{documentId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable int documentId) {
        Document document = documentService.getDocumentById(documentId,0);
        String fileName = document.getName();

        byte[] fileContent = nextcloudService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(document.getName()).build());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
    @GetMapping("/user/{userId}")
    public List<Document> getDocumentsByUserId(@PathVariable int userId) {
        return documentService.getDocumentsByUserId(userId);
    }
}