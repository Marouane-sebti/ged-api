CREATE TABLE ged_user (
    id INTEGER  AUTO_INCREMENT primary key ,
    user_name VARCHAR(30),
    first_name VARCHAR(30),
    last_name VARCHAR(30),
    email VARCHAR(30),
    role VARCHAR(10),
    password VARCHAR(200),
    phone VARCHAR(15)

)
