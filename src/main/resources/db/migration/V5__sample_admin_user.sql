-- Admin user for development testing
-- Email: admin@pageturn.com
-- Password: admin123 (BCrypt hash with strength 12) : https://bcrypt-generator.com/
INSERT INTO users (email, pwd_hash, first_name, last_name, role, created_at, updated_at) VALUES (
    'admin@pageturn.com',
    '$2a$12$4e3VWofbMHS8.mtWORISVuLJQSyYOlntpk1g8kXL5kglt0T2ZidJK',
    'Admin',
    'Pageturn',
    'ADMIN',
    now(),
    now()
);