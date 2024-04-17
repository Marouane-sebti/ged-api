package fr.norsys.gedapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentDto {

    private String name;

    @JsonProperty("is_folder")
    private boolean isFolder;

    @JsonProperty("creation_date")
    private LocalDateTime creationDate;

    private Map<String, String> metadata;

    @JsonProperty("file_path")
    private String filePath;
}