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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class DocumentDao {

    private final JdbcTemplate jdbcTemplate;

    public DocumentDao(JdbcTemplate jdbcTemplate ) {
        this.jdbcTemplate = jdbcTemplate;
    }
//    public Document save(Document document) {
//        String sql = "INSERT INTO documents (id, name, is_folder, creation_date, file_path) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=?, is_folder=?, creation_date=?, file_path=?";
//        jdbcTemplate.update(sql, document.getId(), document.getName(), document.isFolder(), document.getCreationDate(), document.getFilePath(), document.getName(), document.isFolder(), document.getCreationDate(), document.getFilePath());
//        return document;
//    }
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

    public Document createDocument(Document document) {
        String sql = "INSERT INTO documents (id, name, is_folder, creation_date, file_path) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id";
        jdbcTemplate.update(sql, document.getId(), document.getName(), document.isFolder(), document.getCreationDate(), document.getFilePath());
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
            document.setId(rs.getInt("id"));
            document.setName(rs.getString("name"));
            document.setFolder(rs.getBoolean("is_folder"));
            document.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
            document.setMetadata((List<Metadata>) rs.getObject("metadata"));
            document.setFilePath(rs.getString("file_path"));
            return document;
        }
    };

}