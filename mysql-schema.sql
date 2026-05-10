-- ============================================================
-- ParkZen Smart Parking System - MySQL Schema
-- ============================================================
CREATE DATABASE IF NOT EXISTS parkzen_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE parkzen_db;

CREATE TABLE IF NOT EXISTS users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    email          VARCHAR(150)  NOT NULL UNIQUE,
    password       VARCHAR(255)  NOT NULL,
    vehicle_number VARCHAR(20),
    mobile_number  VARCHAR(15),
    role           ENUM('ROLE_USER','ROLE_OWNER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS admins (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     ENUM('ROLE_USER','ROLE_OWNER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_ADMIN',
    INDEX idx_admins_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS owners (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL,
    parking_area_name VARCHAR(200),
    address           VARCHAR(500),
    latitude          DOUBLE,
    longitude         DOUBLE,
    email             VARCHAR(150) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    approved          TINYINT(1)   NOT NULL DEFAULT 0,
    role              ENUM('ROLE_USER','ROLE_OWNER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_OWNER',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_owners_email (email),
    INDEX idx_owners_approved (approved)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS parking_areas (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(200) NOT NULL,
    address                 VARCHAR(500) NOT NULL,
    latitude                DOUBLE       NOT NULL,
    longitude               DOUBLE       NOT NULL,
    total_slots             INT          NOT NULL DEFAULT 0,
    available_slots         INT          NOT NULL DEFAULT 0,
    price_per_hour          DOUBLE       NOT NULL,
    charging_available      TINYINT(1)   NOT NULL DEFAULT 0,
    charging_price_per_hour DOUBLE       NOT NULL DEFAULT 0.0,
    status                  VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    owner_id                BIGINT       NOT NULL,
    CONSTRAINT fk_parking_owner FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE,
    INDEX idx_parking_owner    (owner_id),
    INDEX idx_parking_status   (status),
    INDEX idx_parking_location (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS parking_slots (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_number         VARCHAR(20)  NOT NULL,
    slot_type           VARCHAR(20)  NOT NULL DEFAULT 'CAR',
    availability_status ENUM('AVAILABLE','BOOKED','MAINTENANCE','DISABLED') NOT NULL DEFAULT 'AVAILABLE',
    charging_enabled    TINYINT(1)   NOT NULL DEFAULT 0,
    parking_area_id     BIGINT       NOT NULL,
    CONSTRAINT fk_slot_area FOREIGN KEY (parking_area_id) REFERENCES parking_areas(id) ON DELETE CASCADE,
    INDEX idx_slot_area   (parking_area_id),
    INDEX idx_slot_status (availability_status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bookings (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    start_time      DATETIME NOT NULL,
    end_time        DATETIME NOT NULL,
    duration_hours  DOUBLE   NOT NULL,
    total_amount    DOUBLE   NOT NULL,
    booking_status  ENUM('PENDING','CONFIRMED','ACTIVE','COMPLETED','CANCELLED','EXTENDED') NOT NULL DEFAULT 'PENDING',
    qr_code         LONGTEXT,
    extendable      TINYINT(1) NOT NULL DEFAULT 1,
    user_id         BIGINT   NOT NULL,
    parking_slot_id BIGINT   NOT NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id)         REFERENCES users(id),
    CONSTRAINT fk_booking_slot FOREIGN KEY (parking_slot_id) REFERENCES parking_slots(id),
    INDEX idx_booking_user       (user_id),
    INDEX idx_booking_slot       (parking_slot_id),
    INDEX idx_booking_status     (booking_status),
    INDEX idx_booking_time_range (start_time, end_time)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    razorpay_order_id   VARCHAR(100) UNIQUE,
    razorpay_payment_id VARCHAR(100),
    amount              DOUBLE       NOT NULL,
    payment_status      ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_time        DATETIME,
    booking_id          BIGINT       NOT NULL UNIQUE,
    owner_id            BIGINT,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_payment_owner   FOREIGN KEY (owner_id)   REFERENCES owners(id),
    INDEX idx_payment_order  (razorpay_order_id),
    INDEX idx_payment_owner  (owner_id),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS feedbacks (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating     INT      NOT NULL,
    comment    TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id    BIGINT   NOT NULL,
    owner_id   BIGINT   NOT NULL,
    CONSTRAINT fk_feedback_user  FOREIGN KEY (user_id)  REFERENCES users(id),
    CONSTRAINT fk_feedback_owner FOREIGN KEY (owner_id) REFERENCES owners(id),
    INDEX idx_feedback_owner (owner_id),
    INDEX idx_feedback_user  (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS complaints (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject          VARCHAR(255) NOT NULL,
    message          TEXT         NOT NULL,
    complaint_status ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id          BIGINT       NOT NULL,
    owner_id         BIGINT,
    CONSTRAINT fk_complaint_user  FOREIGN KEY (user_id)  REFERENCES users(id),
    CONSTRAINT fk_complaint_owner FOREIGN KEY (owner_id) REFERENCES owners(id),
    INDEX idx_complaint_user   (user_id),
    INDEX idx_complaint_status (complaint_status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS messages (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_role   VARCHAR(20) NOT NULL,
    receiver_role VARCHAR(20) NOT NULL,
    message       TEXT        NOT NULL,
    sender_id     BIGINT,
    receiver_id   BIGINT,
    sent_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_message_receiver (receiver_id),
    INDEX idx_message_sent_at  (sent_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(255) NOT NULL,
    message           TEXT         NOT NULL,
    notification_type ENUM('BOOKING_CONFIRMED','BOOKING_CANCELLED','BOOKING_EXTENDED',
                           'PAYMENT_SUCCESS','PAYMENT_FAILED','PARKING_FULL',
                           'FIRE_ALERT','GENERAL') NOT NULL DEFAULT 'GENERAL',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id           BIGINT       NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notification_user    (user_id),
    INDEX idx_notification_created (created_at)
) ENGINE=InnoDB;

-- Default admin (password: admin@123)
INSERT IGNORE INTO admins (email, password, role)
VALUES ('admin@parkzen.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeNOdqKGJo.MNulSK',
        'ROLE_ADMIN');
