CREATE DATABASE IF NOT EXISTS barberturn
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'barberturn'@'%' IDENTIFIED BY 'barberturn';
CREATE USER IF NOT EXISTS 'barberturn'@'localhost' IDENTIFIED BY 'barberturn';

GRANT ALL PRIVILEGES ON barberturn.* TO 'barberturn'@'%';
GRANT ALL PRIVILEGES ON barberturn.* TO 'barberturn'@'localhost';

FLUSH PRIVILEGES;
