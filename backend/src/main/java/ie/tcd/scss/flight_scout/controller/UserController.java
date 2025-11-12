package ie.tcd.scss.flight_scout.controller;

import ie.tcd.scss.flight_scout.model.User;
import ie.tcd.scss.flight_scout.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")  
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Save a new user
     *
     @param User the User to save
     */
    @GetMapping("/saveUser")
    public ResponseEntity<User> saveUser(
            @RequestParam User newUser
            ) {

        User newUserResponse = userService.saveUser(newUser);

        return ResponseEntity.ok(newUserResponse);
    }
}
