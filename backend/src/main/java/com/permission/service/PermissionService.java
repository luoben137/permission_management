package com.permission.service;

import com.permission.dto.PermissionDTO;
import com.permission.entity.Permission;
import com.permission.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public Page<Permission> findAll(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限点不存在"));
    }

    @Transactional
    public Permission create(PermissionDTO dto) {
        if (permissionRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("权限代码已存在");
        }

        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setUrl(dto.getUrl());
        permission.setMethod(dto.getMethod());
        permission.setDescription(dto.getDescription());

        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission update(Long id, PermissionDTO dto) {
        Permission permission = findById(id);

        if (dto.getCode() != null && !dto.getCode().equals(permission.getCode())) {
            if (permissionRepository.existsByCode(dto.getCode())) {
                throw new RuntimeException("权限代码已存在");
            }
            permission.setCode(dto.getCode());
        }

        if (dto.getName() != null) {
            permission.setName(dto.getName());
        }
        if (dto.getUrl() != null) {
            permission.setUrl(dto.getUrl());
        }
        if (dto.getMethod() != null) {
            permission.setMethod(dto.getMethod());
        }
        if (dto.getDescription() != null) {
            permission.setDescription(dto.getDescription());
        }

        return permissionRepository.save(permission);
    }

    @Transactional
    public void delete(Long id) {
        Permission permission = findById(id);
        permissionRepository.delete(permission);
    }
}

