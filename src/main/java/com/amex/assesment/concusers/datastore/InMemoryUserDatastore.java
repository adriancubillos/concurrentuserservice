package com.amex.assesment.concusers.datastore;

import com.amex.assesment.concusers.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe, in-memory implementation of the {@link UserDatastore}
 * interface.
 * This class uses {@link ConcurrentHashMap} and {@link AtomicLong} to ensure
 * safe
 * concurrent access without explicit locking.
 */
@Repository
public class InMemoryUserDatastore implements UserDatastore {

    // Thread-safe map to store users
    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    // Thread-safe counter to generate unique IDs
    private final AtomicLong counter = new AtomicLong();

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            long id = counter.incrementAndGet();
            user.setId(id);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }
}