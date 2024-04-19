package fr.norsys.gedapi.dao;

import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.DocumentSearchCriteria;
import fr.norsys.gedapi.model.Metadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class DocumentDao {

    private final JdbcTemplate jdbcTemplate;

    public DocumentDao(JdbcTemplate jdbcTemplate ) {
        this.jdbcTemplate = jdbcTemplate;
    }
public Document save(Document document) {
    String sql = "INSERT INTO documents (name, is_folder, creation_date, file_path,hash_value,size,type,user_id) VALUES (?,?,?,?, ?, ?, ?,?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, document.getName());
        ps.setBoolean(2, document.isFolder());
        ps.setTimestamp(3, Timestamp.valueOf(document.getCreationDate()));
        ps.setString(4, document.getFilePath());
        ps.setString(5, document.getHashValue());
        ps.setLong(6, document.getSize());
        ps.setString(7, document.getType());
        ps.setInt(8, document.getUserId());
        return ps;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key != null) {
        document.setId(key.intValue());
    }
    return document;
}

    public Document getDocumentById(int id, int userId) {
        String sql = "SELECT d.*, m.key, m.value FROM documents d LEFT JOIN metadata m ON d.id = m.document_id WHERE d.id = ? AND d.user_id = ?";

        Map<Integer, Document> documentMap = new HashMap<>();

        jdbcTemplate.query(sql, new Object[]{id, userId}, (rs, rowNum) -> {
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

            Metadata metadata = new Metadata();
            metadata.setKey(rs.getString("key"));
            metadata.setValue(rs.getString("value"));

            document.getMetadata().add(metadata);

            return document;
        });

        return documentMap.values().stream().findFirst().orElse(null);
    }

    public void deleteDocument(int id) {
        String deleteMetadataSql = "DELETE FROM metadata WHERE document_id = ?";
        jdbcTemplate.update(deleteMetadataSql, id);
        String deleteDocumentSql = "DELETE FROM documents WHERE id = ?";
        jdbcTemplate.update(deleteDocumentSql, id);
    }
    public Document getByHash(String hash) {
        String sql = "SELECT * FROM documents WHERE hash_value = ?";

        List<Document> documents = jdbcTemplate.query(sql, new Object[]{hash}, documentRowMapper);

        return documents.isEmpty() ? null : documents.get(0);
    }
    public List<Document> getDocumentsByUserId(int userId) {
        String sql = "SELECT * FROM documents WHERE user_id = ?";

        return jdbcTemplate.query(sql, new Object[]{userId}, documentRowMapper);
    }

    public List<Document> searchDocuments(DocumentSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM documents WHERE 1=1");

        List<Object> params = new ArrayList<>();
        if (criteria.getName() != null) {
            sql.append(" AND name LIKE ?");
            params.add("%" + criteria.getName() + "%");
        }
        if (criteria.getIsFolder() != null) {
            sql.append(" AND is_folder = ?");
            params.add(criteria.getIsFolder());
        }
        if (criteria.getCreationDateFrom() != null) {
            sql.append(" AND creation_date >= ?");
            params.add(criteria.getCreationDateFrom());
        }
        if (criteria.getCreationDateTo() != null) {
            sql.append(" AND creation_date <= ?");
            params.add(criteria.getCreationDateTo());
        }
        if (criteria.getType() != null) {
            sql.append(" AND type = ?");
            params.add(criteria.getType());
        }

        if (criteria.getMetadataKey() != null) {
            sql.append(" AND id IN (SELECT document_id FROM metadata WHERE `key` LIKE ?)");
            params.add("%" + criteria.getMetadataKey() + "%");
        }
        if (criteria.getMetadataValue() != null) {
            sql.append(" AND id IN (SELECT document_id FROM metadata WHERE value LIKE ?)");
            params.add("%" + criteria.getMetadataValue() + "%");
        }
        if (criteria.getUserId() != null) {
            sql.append(" AND user_id = ? ");
            params.add(criteria.getUserId());
        }
        return jdbcTemplate.query(sql.toString(), params.toArray(), documentRowMapper);
    }
    private RowMapper<Document> documentRowMapper = new RowMapper<Document>() {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document document = new Document();
            document.setId(rs.getInt("id"));
            document.setName(rs.getString("name"));
            document.setFolder(rs.getBoolean("is_folder"));
            document.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
            document.setFilePath(rs.getString("file_path"));
            document.setSize(rs.getLong("size"));
            document.setType(rs.getString("type"));
            document.setUserId(rs.getInt("user_id"));
            return document;
        }
    };

    public List<Document> getAllDocuments(){
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
                document.setSize(rs.getLong("size"));
                document.setType(rs.getString("type"));
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