package com.permission.controller;

import com.permission.dto.ApiResponse;
import com.permission.dto.PermissionDTO;
import com.permission.entity.Permission;
import com.permission.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Permission>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Permission> permissions = permissionService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Permission>> getById(@PathVariable Long id) {
        Permission permission = permissionService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(permission));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Permission>> create(@Valid @RequestBody PermissionDTO dto) {
        Permission permission = permissionService.create(dto);
        return ResponseEntity.ok(ApiResponse.success("创建成功", permission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Permission>> update(@PathVariable Long id, @Valid @RequestBody PermissionDTO dto) {
        Permission permission = permissionService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success("更新成功", permission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}

