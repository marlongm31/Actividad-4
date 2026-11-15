CREATE TABLE services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE appointments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_id BIGINT NOT NULL,
    service_name VARCHAR(120) NOT NULL,
    service_price DECIMAL(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    customer_phone VARCHAR(30) NOT NULL,
    customer_email VARCHAR(150) NULL,
    notes VARCHAR(500) NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_appointments_service FOREIGN KEY (service_id) REFERENCES services (id),
    INDEX idx_appointments_date (date, start_time),
    INDEX idx_appointments_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE business_hours (
    id BIGINT NOT NULL AUTO_INCREMENT,
    day_of_week INT NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_business_hours_day UNIQUE (day_of_week)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE admin_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    name VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_admin_users_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
