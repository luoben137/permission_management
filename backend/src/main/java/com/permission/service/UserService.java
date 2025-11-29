package com.permission.service;

import com.permission.dto.UserDTO;
import com.permission.entity.Role;
import com.permission.entity.User;
import com.permission.repository.RoleRepository;
import com.permission.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        // Load roles and permissions to avoid LazyInitializationException
        users.getContent().forEach(user -> {
            user.getRoles().forEach(role -> role.getPermissions().size());
        });
        return users;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // Load roles and permissions to avoid LazyInitializationException
        user.getRoles().forEach(role -> role.getPermissions().size());
        return user;
    }

    @Transactional
    public User create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        user.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserDTO dto) {
        User user = findById(id);

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getEnabled() != null) {
            user.setEnabled(dto.getEnabled());
        }

        if (dto.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        if ("admin".equals(user.getUsername())) {
            throw new RuntimeException("不能删除admin用户");
        }
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

