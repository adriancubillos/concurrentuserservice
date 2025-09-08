package com.amex.assesment.concusers.datastore;

import com.amex.assesment.concusers.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDatastore {

    User save(User user);

    Optional<User> findById(long id);

    List<User> findAll();

    boolean existsByEmail(String email);

    void deleteById(long id);
}