
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

    public Metadata(int id, String key, String value) {
    }
}