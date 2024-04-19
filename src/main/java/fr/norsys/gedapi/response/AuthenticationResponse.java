package fr.norsys.gedapi.response;


import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;
    private int userId;



    // builder pattern methods

    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    public static class AuthenticationResponseBuilder {
        private String token;
        private int userId;

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }
        public AuthenticationResponseBuilder userId(int userId) { // new method
            this.userId = userId;
            return this;
        }

        public AuthenticationResponse build() {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(token);
            response.setUserId(userId);
            return response;
        }
    }
}