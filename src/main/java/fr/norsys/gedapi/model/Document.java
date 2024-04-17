package fr.norsys.gedapi.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Document {

    private int id;
    private String name;
    private boolean isFolder;
    private LocalDateTime creationDate;
    private List<Metadata> metadata=new ArrayList<>();

    private String filePath;

}
