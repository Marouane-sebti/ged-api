package fr.norsys.gedapi.model;


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
public class Document {

    private UUID id;
    private String name;
    private boolean isFolder;
    private LocalDateTime creationDate;
    private Map<String, String> metadata;
    private String filePath;
}
