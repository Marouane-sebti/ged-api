CREATE TABLE metadata (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          document_id INT,
                          `key` VARCHAR(255),
                          value VARCHAR(255),
                          FOREIGN KEY (document_id) REFERENCES documents(id)
);