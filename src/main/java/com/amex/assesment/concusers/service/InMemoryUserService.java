package com.amex.assesment.concusers.service;

import com.amex.assesment.concusers.exception.DuplicateUserException;
import com.amex.assesment.concusers.exception.UserNotFoundException;
import com.amex.assesment.concusers.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryUserService implements UserService {

    // Thread-safe map to store users
    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    // Thread-safe counter to generate unique IDs
    private final AtomicLong counter = new AtomicLong();

    // Create a new user
    // Synchronized to ensure thread-safe operation
    // Checks for duplicate email before creating
    // Throws DuplicateUserException if email already exists
    @Override
    public User createUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new DuplicateUserException("User with email " + user.getEmail() + " already exists.");
        }
        // The JVM ensures that the read-increment-write operation is completed as a
        // single, indivisible unit. This guarantees that every call to
        // incrementAndGet() will return a unique ID, even when called by many threads
        // at the exact same time.
        long id = counter.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    // Thread-safe operation

    @Override
    public User getUserById(long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        if (users.remove(id) == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }
}
