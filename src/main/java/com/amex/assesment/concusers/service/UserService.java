package com.amex.assesment.concusers.service;

import com.amex.assesment.concusers.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> getUserById(long id);

    List<User> getAllUsers();

    Optional<User> updateUser(long id, User userDetails);

    boolean deleteUser(long id);
}
