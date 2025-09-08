package com.amex.assesment.concusers.datastore;

import com.amex.assesment.concusers.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for the data access layer (DAL).
 * This interface abstracts the underlying data storage mechanism, allowing for
 * different
 * implementations (e.g., in-memory, JPA, etc.) to be used interchangeably.
 * This design promotes testability and extensibility.
 */
public interface UserDatastore {

    /**
     * Saves a user. If the user is new, it assigns a unique ID.
     * If the user already exists, it updates the existing record.
     *
     * @param user The user to save.
     * @return The saved user with its ID.
     */
    User save(User user);

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to find.
     * @return An Optional containing the user if found, otherwise empty.
     */
    Optional<User> findById(long id);

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    List<User> findAll();

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return true if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user with the given email already exists, excluding a specific
     * user ID.
     *
     * @param email           The email to check.
     * @param userIdToExclude The ID of the user to exclude from the check.
     * @return true if a user with the email exists (excluding the specified user
     *         ID), false otherwise.
     */
    boolean existsByEmailAndIdNot(String email, long userIdToExclude);

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    void deleteById(long id);

    /**
     * Clears all users from the datastore. Used for testing purposes.
     */
    void clear();
}