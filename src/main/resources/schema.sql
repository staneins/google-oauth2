CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       login VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL
);