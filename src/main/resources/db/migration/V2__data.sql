-- Insert roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (username, password, enabled) VALUES
('maxtkachenko', '$2a$10$tC8jvECAWocIy6bt6eAZRO6GCnP1qOptB7gvjtyNbDhJLGtdj2D8a', true), -- Password: password1
('johnmattews', '$2a$10$5t6eGHDdJVVYD9iMRED7vuyIluiGtbaPb7FIaUdwJ5GnnqUYxFk5S', true), -- Password: password2
('tonnyfellow', '$2a$10$fpfPMepnNAu3FJjWedD0EunXbbY9GR6NADfKE3h7JYntCKtjjP0hG', true); -- Password: password3

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2), -- user1 with ROLE_ADMIN
(2, 1), -- user2 with ROLE_USER
(3, 1); -- user3 with ROLE_USER

