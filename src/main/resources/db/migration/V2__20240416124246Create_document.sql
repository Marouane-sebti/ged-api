CREATE TABLE documents (
                           id INT AUTO_INCREMENT,
                           name VARCHAR(255),
                           is_folder BOOLEAN,
                           creation_date TIMESTAMP,
                           file_path VARCHAR(255),
                           PRIMARY KEY (id)
);