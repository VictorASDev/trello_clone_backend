package com.victor.trello_clone.service;

import com.victor.trello_clone.model.User;
import com.victor.trello_clone.model.enums.Role;
import com.victor.trello_clone.repository.UserRepository;
import com.victor.trello_clone.data.record.SignUpRequest;
import jakarta.persistence.EntityNotFoundException;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    public void create(SignUpRequest request) {

        if (!StringUtils.hasText(request.email()) ||
                !StringUtils.hasText(request.password()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");

        if (repository.findByEmail(request.email()).isPresent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with email " + request.email() + " already exists");

        if (repository.findByUsername(request.username()).isPresent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with username " + request.username() + " already exists");

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setUsername(request.username());
        user.setRoles(Set.of(Role.USER));

        repository.save(user);
    }

    public User findByEmail(String userEmail) {
        return repository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found on data!"));
    }

    public Optional<User> findOptionalByEmail(String userEmail) {
        return repository.findByEmail(userEmail);
    }

    public void save(User user) {
        repository.save(user);
    }
}
