package ie.tcd.scss.flight_scout.controller;

import ie.tcd.scss.flight_scout.model.User;
import ie.tcd.scss.flight_scout.service.UserManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserManagementService userService;

    /**
     * Save a new user
     *
     @param User the User to save
     */
    @PostMapping("/api/user/saveUser")
    public ResponseEntity<User> saveUser(
            @RequestBody User newUser
    ) {

        User newUserResponse = userService.saveUser(newUser);

        return ResponseEntity.ok(newUserResponse);
    }

    @GetMapping("/user")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("Hello user");
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
