package com.amex.assesment.concusers.service;

import com.amex.assesment.concusers.datastore.UserDatastore;
import com.amex.assesment.concusers.exception.DuplicateUserException;
import com.amex.assesment.concusers.exception.UserNotFoundException;
import com.amex.assesment.concusers.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMemoryUserService implements UserService {

    private final UserDatastore userDatastore;

    public InMemoryUserService(UserDatastore userDatastore) {
        this.userDatastore = userDatastore;
    }

    @Override
    public User createUser(User user) {
        if (userDatastore.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("User with email " + user.getEmail() + " already exists.");
        }
        return userDatastore.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userDatastore.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userDatastore.findAll();
    }

    @Override
    public User updateUser(long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userDatastore.save(user);
    }

    @Override
    public User updateUserEmail(long id, String email) {
        if (userDatastore.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateUserException("Email " + email + " is already in use by another user.");
        }
        User user = getUserById(id);
        user.setEmail(email);
        return userDatastore.save(user);
    }

    @Override
    public void deleteUser(long id) {
        if (userDatastore.findById(id).isEmpty()) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userDatastore.deleteById(id);
    }
}
