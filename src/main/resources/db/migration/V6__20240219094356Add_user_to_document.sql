ALTER TABLE documents
ADD COLUMN user_id INT,
ADD FOREIGN KEY (user_id) REFERENCES ged_user(id);