package fr.norsys.gedapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class NextcloudService {

    private final String serverUrl;
    private final String username;
    private final String password;
    private final RestTemplate restTemplate;

    public NextcloudService(@Value("${nextcloud.serverUrl}") String serverUrl,
                            @Value("${nextcloud.username}") String username,
                            @Value("${nextcloud.password}") String password,
                            RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
        this.restTemplate = restTemplate;

        ClientHttpRequestInterceptor basicAuthInterceptor = new BasicAuthenticationInterceptor(this.username, this.password);
        this.restTemplate.getInterceptors().add(basicAuthInterceptor);
    }

    public String uploadFile(byte[] fileData, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileData, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                serverUrl + "remote.php/dav/files/" + username + "/" + fileName,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return serverUrl + "remote.php/dav/files/" + username + "/" + fileName;
        } else {
            throw new RuntimeException("Failed to upload file to Nextcloud: " + responseEntity.getBody());
        }
    }

    public byte[] downloadFile(String fileName) {
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                serverUrl + "remote.php/dav/files/" + username + "/" + fileName,
                HttpMethod.GET,
                null,
                byte[].class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("File downloaded successfully!");
            return responseEntity.getBody();
        } else {
            System.err.println("Failed to download file: " + responseEntity.getBody());
            return null;
        }
    }

    public void createFolder(String folderName) {
        try {
            RequestEntity<Void> requestEntity = RequestEntity
                    .method(HttpMethod.valueOf("MKCOL"), new URI(serverUrl + "remote.php/dav/files/" + username + "/" + folderName))
                    .build();

            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("Folder created successfully!");
            } else {
                System.err.println("Failed to create folder: " + responseEntity.getBody());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
        }
    }

    public boolean fileExists(String fileName) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                serverUrl + "remote.php/dav/files/" + username + "/" + fileName,
                HttpMethod.HEAD,
                null,
                String.class
        );

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    public void deleteFile(String fileName) {
        restTemplate.delete(serverUrl + "remote.php/dav/files/" + username + "/" + fileName);
        System.out.println("File deleted successfully!");
    }
}