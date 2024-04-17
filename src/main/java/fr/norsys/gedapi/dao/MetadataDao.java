package fr.norsys.gedapi.dao;

import fr.norsys.gedapi.model.Metadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MetadataDao {

    private final JdbcTemplate jdbcTemplate;

    public MetadataDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Metadata> saveAll(List<Metadata> metadataList) {
        String sql = "INSERT INTO metadata (document_id, `key`, value) VALUES (?, ?, ?)";

        for (Metadata metadata : metadataList) {
            jdbcTemplate.update(sql, metadata.getDocumentId(), metadata.getKey(), metadata.getValue());
        }

        return metadataList;
    }

    public List<Metadata> findByDocumentId(int documentId) {
        String sql = "SELECT * FROM metadata WHERE document_id = ?";
        return jdbcTemplate.query(sql, new Object[]{documentId}, metadataRowMapper);
    }

    private RowMapper<Metadata> metadataRowMapper = new RowMapper<Metadata>() {
        @Override
        public Metadata mapRow(ResultSet rs, int rowNum) throws SQLException {
            Metadata metadata = new Metadata();
            metadata.setId(rs.getInt("id"));
            metadata.setDocumentId(rs.getInt("document_id"));
            metadata.setKey(rs.getString("key"));
            metadata.setValue(rs.getString("value"));
            return metadata;
        }
    };
}