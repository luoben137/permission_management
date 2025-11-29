package com.permission.service;

import com.permission.dto.RoleDTO;
import com.permission.entity.Permission;
import com.permission.entity.Role;
import com.permission.repository.PermissionRepository;
import com.permission.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
    }

    @Transactional
    public Role create(RoleDTO dto) {
        if (roleRepository.existsByName(dto.getName())) {
            throw new RuntimeException("角色名已存在");
        }

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(dto.getPermissionIds()));
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    @Transactional
    public Role update(Long id, RoleDTO dto) {
        Role role = findById(id);

        if (dto.getName() != null && !dto.getName().equals(role.getName())) {
            if (roleRepository.existsByName(dto.getName())) {
                throw new RuntimeException("角色名已存在");
            }
            role.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }

        if (dto.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(dto.getPermissionIds()));
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    @Transactional
    public void delete(Long id) {
        Role role = findById(id);
        roleRepository.delete(role);
    }
}

