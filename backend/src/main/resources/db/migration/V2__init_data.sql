-- Insert default permissions
INSERT INTO permissions (name, code, url, method, description) VALUES
('用户管理', 'USER_MANAGE', '/api/users/**', '*', '用户管理权限'),
('角色管理', 'ROLE_MANAGE', '/api/roles/**', '*', '角色管理权限'),
('权限点管理', 'PERMISSION_MANAGE', '/api/permissions/**', '*', '权限点管理权限'),
('审计日志查看', 'AUDIT_VIEW', '/api/audit-logs/**', 'GET', '审计日志查看权限')
ON CONFLICT (code) DO NOTHING;

-- Insert admin role
INSERT INTO roles (name, description) VALUES
('ADMIN', '管理员角色，拥有所有权限')
ON CONFLICT (name) DO NOTHING;

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Insert admin user (password: 1234, BCrypt hash)
-- BCrypt hash for "1234" with default rounds
INSERT INTO users (username, password, enabled) VALUES
('admin', '$2a$10$kyF6Wq0kB8ZDJmr5J3fnOumg6abCtz.fbhWT9WSXnjDrmP4qsp3/i', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

