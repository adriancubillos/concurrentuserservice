package com.amex.assesment.concusers.service;

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

    private final ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    @Override
    public User createUser(User user) {
        long id = counter.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> updateUser(long id, User userDetails) {
        return Optional.ofNullable(users.computeIfPresent(id, (key, existingUser) -> {
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            return existingUser;
        }));
    }

    @Override
    public boolean deleteUser(long id) {
        return users.remove(id) != null;
    }
}
