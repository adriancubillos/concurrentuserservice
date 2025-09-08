package com.amex.assesment.concusers.service;

import com.amex.assesment.concusers.exception.UserNotFoundException;
import com.amex.assesment.concusers.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(long id) throws UserNotFoundException;

    List<User> getAllUsers();

    User updateUser(long id, User userDetails) throws UserNotFoundException;

    User updateUserEmail(long id, String email) throws UserNotFoundException;

    void deleteUser(long id) throws UserNotFoundException;
}
