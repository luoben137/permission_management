package com.permission.controller;

import com.permission.dto.ApiResponse;
import com.permission.dto.RoleDTO;
import com.permission.entity.Role;
import com.permission.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Role>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Role> roles = roleService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getById(@PathVariable Long id) {
        Role role = roleService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(role));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@Valid @RequestBody RoleDTO dto) {
        Role role = roleService.create(dto);
        return ResponseEntity.ok(ApiResponse.success("创建成功", role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> update(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        Role role = roleService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success("更新成功", role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}

