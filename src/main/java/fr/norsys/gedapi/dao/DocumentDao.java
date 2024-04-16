package fr.norsys.gedapi.dao;

import fr.norsys.gedapi.model.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Repository
public class DocumentDao {

    private final JdbcTemplate jdbcTemplate;

    public DocumentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Document save(Document document) {
        String sql = "INSERT INTO documents (id, name, is_folder, creation_date, metadata, file_path) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=?, is_folder=?, creation_date=?, metadata=?, file_path=?";
        jdbcTemplate.update(sql, document.getId(), document.getName(), document.isFolder(), document.getCreationDate(), document.getMetadata(), document.getFilePath(), document.getName(), document.isFolder(), document.getCreationDate(), document.getMetadata(), document.getFilePath());
        return document;
    }

    public Document createDocument(Document document) {
        String sql = "INSERT INTO documents (id, name, is_folder, creation_date, metadata, file_path) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        jdbcTemplate.update(sql, document.getId(), document.getName(), document.isFolder(), document.getCreationDate(), document.getMetadata(), document.getFilePath());
        return document;
    }
    public Document getDocumentById(UUID id) {
        String sql = "SELECT * FROM documents WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, documentRowMapper);
    }

    public void updateDocument(Document document) {
        String sql = "UPDATE documents SET name = ?, is_folder = ?, creation_date = ?, metadata = ?, file_path = ? WHERE id = ?";
        jdbcTemplate.update(sql, document.getName(), document.isFolder(), document.getCreationDate(), document.getMetadata(), document.getFilePath(), document.getId());
    }

    public void deleteDocument(UUID id) {
        String sql = "DELETE FROM documents WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Document> documentRowMapper = new RowMapper<Document>() {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document document = new Document();
            document.setId(UUID.fromString(rs.getString("id")));
            document.setName(rs.getString("name"));
            document.setFolder(rs.getBoolean("is_folder"));
            document.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
            document.setMetadata((Map<String, String>) rs.getObject("metadata"));
            document.setFilePath(rs.getString("file_path"));
            return document;
        }
    };
}