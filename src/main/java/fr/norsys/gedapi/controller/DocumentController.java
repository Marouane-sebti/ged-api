package fr.norsys.gedapi.controller;


import fr.norsys.gedapi.dao.DocumentDao;
import fr.norsys.gedapi.dto.MetadataDto;
import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.DocumentSearchCriteria;
import fr.norsys.gedapi.model.Metadata;
import fr.norsys.gedapi.service.DocumentService;
import fr.norsys.gedapi.service.NextcloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class DocumentController {

    private final NextcloudService nextcloudService;
    private final DocumentService documentService;
    private final DocumentDao documentDao;
    private final JdbcTemplate jdbcTemplate;

    public DocumentController(NextcloudService nextcloudService, DocumentService documentService, DocumentDao documentDao, JdbcTemplate jdbcTemplate) {
        this.nextcloudService = nextcloudService;
        this.documentService = documentService;
        this.documentDao = documentDao;
        this.jdbcTemplate = jdbcTemplate;
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

    @PutMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> createDocument(
            @RequestParam("file") MultipartFile file,
            @RequestPart("metadata") List<MetadataDto> metadataDtoList) {
        try {
            Document createdDocument = documentService.createDocument(metadataDtoList, file);
            return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable int id) {
        Document document = documentService.getDocument(id);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable int id) {
        documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    @PostMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(@RequestBody DocumentSearchCriteria criteria) {
        List<Document> documents = documentDao.searchDocuments(criteria);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }
    @GetMapping
    public List<Document> getAllDocuments() {
        String sql = "SELECT d.*, m.key, m.value FROM documents d LEFT JOIN metadata m ON d.id = m.document_id";
        Map<Integer, Document> documentMap = new HashMap<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            Integer documentId = rs.getInt("id");
            Document document = documentMap.get(documentId);
            if (document == null) {
                document = new Document();
                document.setId(documentId);
                document.setName(rs.getString("name"));
                document.setFolder(rs.getBoolean("is_folder"));
                document.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                document.setFilePath(rs.getString("file_path"));
                documentMap.put(documentId, document);
            }
            String key = rs.getString("key");
            if (key != null) {
                Metadata metadata = new Metadata();
                metadata.setKey(key);
                metadata.setValue(rs.getString("value"));

                document.getMetadata().add(metadata);
            }
            return document;
        });
        return new ArrayList<>(documentMap.values());
    }
}