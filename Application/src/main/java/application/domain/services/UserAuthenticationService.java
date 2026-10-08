package application.domain.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Administrator;
import application.domain.models.LogisticsOperator;
import application.domain.models.Person;
import application.domain.models.Supervisor;
import application.domain.models.SystemUser;
import application.domain.ports.in.ChangeUserStatusUseCase;
import application.domain.ports.in.ConsultUserUseCase;
import application.domain.ports.in.LoginUseCase;
import application.domain.ports.in.LogoutUseCase;
import application.domain.ports.in.RegisterCustomerUserUseCase;
import application.domain.ports.in.RegisterEmployeeUserUseCase;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.PasswordHasher;
import application.domain.ports.out.SellerRepository;
import application.domain.ports.out.UserRepository;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Domain Service (User and Authentication subdomain): creates and manages
 * system identities (customer users and employee users), and enforces login
 * and logout rules.
 *
 * Business rules enforced:
 * - A customer system user must be associated with an existing Person.
 * - Registering employees is restricted to the ADMINISTRADOR role.
 * - Login requires an ACTIVO person; sessions are opaque tokens.
 */
public class UserAuthenticationService implements LoginUseCase, LogoutUseCase,
        RegisterCustomerUserUseCase, RegisterEmployeeUserUseCase,
        ConsultUserUseCase, ChangeUserStatusUseCase {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final PasswordHasher passwordHasher;
    private final Map<String, Administrator> administratorDirectory =
            new ConcurrentHashMap<>();

    public UserAuthenticationService(UserRepository userRepository,
                                     BuyerRepository buyerRepository,
                                     SellerRepository sellerRepository,
                                     PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.passwordHasher = passwordHasher;
    }

    /** Registers a known administrator (employee registration authorizer). */
    public UserAuthenticationService registerAdministrator(
            Administrator administrator) {
        administratorDirectory.put(administrator.getIdentifier(), administrator);
        return this;
    }

    @Override
    public SystemUser registerCustomerUser(String personId, String username,
                                           String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already registered: " + username);
        }
        Person person = resolveCustomer(personId);
        SystemUser user = new SystemUser(username,
                passwordHasher.encode(rawPassword), person);
        return userRepository.save(user);
    }

    @Override
    public SystemUser registerEmployeeUser(String administratorId,
                                           String roleCode,
                                           String identifier, String fullName,
                                           String email, String username,
                                           String rawPassword) {
        Administrator administrator = requireAdministrator(administratorId);
        if (!SystemRole.ADMINISTRADOR.equals(administrator.getRole())) {
            throw new IllegalStateException(
                    "Registering employees is restricted to the ADMINISTRADOR role");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already registered: " + username);
        }
        Person staff = buildStaff(roleCode, identifier, fullName, email);
        SystemUser user = new SystemUser(username,
                passwordHasher.encode(rawPassword), staff);
        return userRepository.save(user);
    }
@Override
    public SystemUser login(String username, String rawPassword) {
        SystemUser user = requireUser(username);
        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Invalid credentials for user: " + username);
        }
        user.startSession(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    @Override
    public void logout(String username) {
        SystemUser user = requireUser(username);
        user.endSession();
        userRepository.save(user);
    }

    @Override
    public SystemUser consultUser(String username) {
        return requireUser(username);
    }

    @Override
    public SystemUser changeUserStatus(String username, String statusCode) {
        SystemUser user = requireUser(username);
        UserStatus status = UserStatus.fromCode(statusCode);
        user.getPerson().setStatus(status);
        if (!UserStatus.ACTIVO.equals(status) && user.isLoggedIn()) {
            user.endSession();
        }
        return userRepository.save(user);
    }

    private Person resolveCustomer(String personId) {
        return buyerRepository.findById(personId)
                .<Person>map(customer -> customer)
                .or(() -> sellerRepository.findById(personId)
                        .<Person>map(customer -> customer))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + personId));
    }

    private Person buildStaff(String roleCode, String identifier,
                              String fullName, String email) {
        switch (roleCode) {
            case "ADMINISTRADOR":
                return new Administrator(identifier, fullName, email,
                        UserStatus.ACTIVO);
            case "OPERADOR_LOGISTICO":
                return new LogisticsOperator(identifier, fullName, email,
                        UserStatus.ACTIVO);
            case "SUPERVISOR":
                return new Supervisor(identifier, fullName, email,
                        UserStatus.ACTIVO);
            default:
                throw new IllegalArgumentException(
                        "Unknown employee role: " + roleCode);
        }
    }

    private Administrator requireAdministrator(String administratorId) {
        Administrator administrator = administratorDirectory.get(administratorId);
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Administrator not registered: " + administratorId);
        }
        return administrator;
    }

    private SystemUser requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + username));
    }
}