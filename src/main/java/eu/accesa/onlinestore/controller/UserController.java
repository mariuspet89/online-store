package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.ResetLinkDto;
import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/existsByUsername")
    public ResponseEntity<Boolean> existsByUsername(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.existsByUsername(username));
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<UserDto> findByUsername(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(username));
    }

    @GetMapping("/existsByEmail")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.existsByEmail(email));
    }

    @GetMapping("/findByEmail")
    public ResponseEntity<UserDto> findByEmail(@RequestParam() String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByEmail(email));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Successfully added a user.")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDtoNoId userDtoNoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDtoNoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @Valid @RequestBody UserDtoNoId userDtoNoId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, userDtoNoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User Deleted");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ResetLinkDto> forgotPassword(@RequestParam String email) {
        String response = userService.forgotPassword(email);
        if (!response.isEmpty()) {
            response = "http://localhost:8080/users/reset-password?token=" + response;
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResetLinkDto(response));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                @RequestParam String password) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.resetPassword(token, password));
    }
}
