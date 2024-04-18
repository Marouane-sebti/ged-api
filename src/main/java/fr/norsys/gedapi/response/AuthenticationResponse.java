package fr.norsys.gedapi.response;


import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;



    // builder pattern methods

    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    public static class AuthenticationResponseBuilder {
        private String token;

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponse build() {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(token);
            return response;
        }
    }
}