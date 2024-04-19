package fr.norsys.gedapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchCriteria {
    private String id;
    private String name;
    private Boolean isFolder;
    private String type;
    private String creationDateFrom;
    private String creationDateTo;
    private String metadataKey;
    private String metadataValue;
    private Integer userId;

}