
package fr.norsys.gedapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Metadata {

    private int id;
    private int documentId;
    private String key;
    private String value;

    public Metadata(int documentId, String key, String value) {
        this.documentId = documentId;
        this.key = key;
        this.value = value;
    }
}