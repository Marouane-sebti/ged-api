package fr.norsys.gedapi.dao;

import fr.norsys.gedapi.model.Document;
import fr.norsys.gedapi.model.Metadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DocumentDao {

    private final JdbcTemplate jdbcTemplate;

    public DocumentDao(JdbcTemplate jdbcTemplate ) {
        this.jdbcTemplate = jdbcTemplate;
    }
public Document save(Document document) {
    String sql = "INSERT INTO documents (name, is_folder, creation_date, file_path,hash_value) VALUES (?, ?, ?, ?,?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, document.getName());
        ps.setBoolean(2, document.isFolder());
        ps.setTimestamp(3, Timestamp.valueOf(document.getCreationDate()));
        ps.setString(4, document.getFilePath());
        ps.setString(5, document.getHashValue());

        return ps;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key != null) {
        document.setId(key.intValue());
    }
    return document;
}

    public Document getDocumentById(int id) {
        String sql = "SELECT d.*, m.key, m.value FROM documents d LEFT JOIN metadata m ON d.id = m.document_id WHERE d.id = ?";

        Map<Integer, Document> documentMap = new HashMap<>();

        jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
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

    private RowMapper<Document> documentRowMapper = new RowMapper<Document>() {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document document = new Document();
            document.setId(rs.getInt("id"));
            document.setName(rs.getString("name"));
            document.setFolder(rs.getBoolean("is_folder"));
            document.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
            document.setFilePath(rs.getString("file_path"));
            return document;
        }
    };

}