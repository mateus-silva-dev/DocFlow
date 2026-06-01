package com.docflow.example.controller;

import com.docflow.example.dto.UserDTO;
import io.github.docflowlib.docflow.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@ApiDocController // DocFlow intercepts this Controller
public class UserController {

    // Public Route
    @GetMapping
    @ApiDocGet
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = List.of(
                new UserDTO(1L, "Mateus Silva", "mateus@email.com", "ADMIN"),
                new UserDTO(2L, "User Exemplo", "exemplo@email.com", "USER")
        );
        return ResponseEntity.ok(users);
    }

    // Protected Route (DocFlow infers and adds the padlock icon automatically)
    @PostMapping
    @ApiDocPost
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        UserDTO created = new UserDTO(3L, dto.name(), dto.email(), dto.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Deletion Route (Automatic 204 No Content Return)
    @DeleteMapping("/{id}")
    @ApiDocDelete
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
