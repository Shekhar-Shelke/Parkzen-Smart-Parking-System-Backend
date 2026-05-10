-- ParkZen Default Admin Seed
-- Password: admin@123 (BCrypt hashed)
INSERT IGNORE INTO admins (email, password, role)
VALUES ('admin@parkzen.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LeNOdqKGJo.MNulSK', 'ROLE_ADMIN');
