package controller;

import exception.InsufficientFundsException;
import exception.UserNotFoundException;
import jakarta.persistence.Cacheable;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<User> users = userService.searchUsers(name, dateOfBirth, email, phone, page, size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/email")
    public ResponseEntity<String> updateEmail(
            @PathVariable Long userId,
            @RequestParam String oldEmail,
            @RequestParam String newEmail) {
        try {
            User updatedUser = userService.updateEmail(userId, oldEmail, newEmail);
            return ResponseEntity.ok("Email updated successfully to: " + updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{userId}/phone")
    public ResponseEntity<User> updatePhone(@PathVariable Long userId,
                                            @PathVariable String oldPhone,
                                            @RequestParam String newPhone) {
        try {
            User updatedUser = userService.updatePhone(userId,oldPhone, newPhone);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    @Cacheable(value = "usersCache", key = "#id")
    public Optional<User> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/transfer")
    public String transferFunds(@RequestParam Long toUserId, @RequestParam BigDecimal value) throws UserNotFoundException, InsufficientFundsException {
        Long fromUserId = null; // Получите ID из токена
        boolean success = userService.transferFunds(fromUserId, toUserId, value);
        return success ? "Transfer successful" : "Transfer failed";
    }
}


