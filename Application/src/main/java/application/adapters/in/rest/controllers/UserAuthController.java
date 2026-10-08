package application.adapters.in.rest.controllers;

import application.adapters.in.rest.requests.ChangeStatusRequest;
import application.adapters.in.rest.requests.LoginRequest;
import application.adapters.in.rest.requests.RegisterCustomerUserRequest;
import application.adapters.in.rest.requests.RegisterEmployeeUserRequest;
import application.domain.models.SystemUser;
import application.domain.ports.in.ChangeUserStatusUseCase;
import application.domain.ports.in.ConsultUserUseCase;
import application.domain.ports.in.LoginUseCase;
import application.domain.ports.in.LogoutUseCase;
import application.domain.ports.in.RegisterCustomerUserUseCase;
import application.domain.ports.in.RegisterEmployeeUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes user registration, authentication and user
 * status endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class UserAuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RegisterCustomerUserUseCase registerCustomerUserUseCase;
    private final RegisterEmployeeUserUseCase registerEmployeeUserUseCase;
    private final ConsultUserUseCase consultUserUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;

    public UserAuthController(LoginUseCase loginUseCase,
                              LogoutUseCase logoutUseCase,
                              RegisterCustomerUserUseCase registerCustomerUserUseCase,
                              RegisterEmployeeUserUseCase registerEmployeeUserUseCase,
                              ConsultUserUseCase consultUserUseCase,
                              ChangeUserStatusUseCase changeUserStatusUseCase) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.registerCustomerUserUseCase = registerCustomerUserUseCase;
        this.registerEmployeeUserUseCase = registerEmployeeUserUseCase;
        this.consultUserUseCase = consultUserUseCase;
        this.changeUserStatusUseCase = changeUserStatusUseCase;
    }

    @PostMapping("/register/customers")
    public ResponseEntity<SystemUser> registerCustomerUser(
            @RequestBody RegisterCustomerUserRequest request) {
        return ResponseEntity.ok(registerCustomerUserUseCase
                .registerCustomerUser(request.getPersonId(),
                        request.getUsername(), request.getPassword()));
    }

    @PostMapping("/register/employees")
    public ResponseEntity<SystemUser> registerEmployeeUser(
            @RequestBody RegisterEmployeeUserRequest request) {
        return ResponseEntity.ok(registerEmployeeUserUseCase
                .registerEmployeeUser(request.getAdministratorId(),
                        request.getRoleCode(), request.getIdentifier(),
                        request.getFullName(), request.getEmail(),
                        request.getUsername(), request.getPassword()));
    }

    @PostMapping("/login")
    public ResponseEntity<SystemUser> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.login(
                request.getUsername(), request.getPassword()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LoginRequest request) {
        logoutUseCase.logout(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<SystemUser> consultUser(
            @PathVariable String username) {
        return ResponseEntity.ok(consultUserUseCase.consultUser(username));
    }

    @PatchMapping("/users/{username}/status")
    public ResponseEntity<SystemUser> changeUserStatus(
            @PathVariable String username,
            @RequestBody ChangeStatusRequest request) {
        return ResponseEntity.ok(changeUserStatusUseCase
                .changeUserStatus(username, request.getStatusCode()));
    }
}