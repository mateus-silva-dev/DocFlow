package src.main.java.com.docflow.example.controller;

import com.docflow.example.dto.UserDTO;
import io.github.docflowlib.docflow.annotations.ApiDoc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
// DocFlow intercepts this Controller
public class UserController {

    // Public Route
    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = List.of(
                new UserDTO(1L, "Mateus Silva", "mateus@email.com", "ADMIN"),
                new UserDTO(2L, "User Exemplo", "exemplo@email.com", "USER")
        );
        return ResponseEntity.ok(users);
    }

    /**
     * Protected Route (DocFlow infers and adds the padlock icon automatically)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        UserDTO created = new UserDTO(3L, dto.name(), dto.email(), dto.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Deletes a resource by its ID.
     *
     *
     * Since DELETE operations are often idempotent, we might not want to return
     * a 404 error if the resource doesn't exist.
     *
     * The @ApiDoc annotation is used here to explicitly exclude the 404 status
     * from the generated OpenAPI documentation.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiDoc(exclude = {HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR}) // Excludes 404 from Swagger UI
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }


}

