package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User update(User newUser);

    User getById(Long userId);

    void deleteById(Long userId);

    List<User> getAll();
}

