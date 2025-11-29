-- Update admin user password to correct BCrypt hash (password: 1234)
UPDATE users
SET password = '$2a$10$kyF6Wq0kB8ZDJmr5J3fnOumg6abCtz.fbhWT9WSXnjDrmP4qsp3/i'
WHERE username = 'admin';

