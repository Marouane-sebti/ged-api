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
public class User {
    private long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    //private Role role;

}
