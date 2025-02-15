package ru.spbstu.rakitin.administration.service.auth.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.administration.exceptions.UserAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.UserNotFoundException;
import ru.spbstu.rakitin.administration.repository.auth.UserRepository;
import ru.spbstu.rakitin.administration.service.auth.UserService;
import ru.spbstu.rakitin.commonentites.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Long register(User user) throws UserAlreadyExistsException {
        if (userRepository.findFirstByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with name %s already exists!", user.getUsername()));
        }
        log.info("Register new user {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user).getId();
    }

    public void delete(Long id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("User with id %s not found!", id));
        }
        userRepository.delete(userOptional.get());
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findFirstByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with name %s not found!", username)));
    }

    @Override
    public User findUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found!", id)));

    }

    @Override
    public void flipAdmin(long userId, boolean set) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found!", userId)));

        user.setAdmin(set);
        userRepository.save(user);

    }
}
