package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.user.exception.UserRepositoryException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.IdGenerator;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    @Override
    public User create(User user) {
        isEmailExist(user.getEmail())
                .ifPresent((u) -> {
                    throw new UserRepositoryException(user.getEmail() + ": этот ящик уже использовался");
                });
        long id = idGenerator.getId();
        user.setId(id);
        users.put(id, user);
        log.info("Добавлен новый пользователь: {}", user.toString());
        return users.get(id);
    }

    @Override
    public User update(User user) {
        User updatedUser = getById(user.getId());
        if (StringUtils.isNotBlank(user.getEmail())) {
            isEmailExist(user.getEmail())
                    .ifPresent(u -> {
                        if (!u.getId().equals(updatedUser.getId())) {
                            throw new UserRepositoryException(user.getEmail() + ": этот ящик уже использовался");
                        }
                    });
            updatedUser.setEmail(user.getEmail());
        }
        if (StringUtils.isNotBlank(user.getName())) {
            updatedUser.setName(user.getName());
        }
        log.info("Данные пользователя обновлены: {}", updatedUser.toString());
        return users.get(updatedUser.getId());
    }

    @Override
    public User getById(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseGet(() -> {
                    throw new UserRepositoryException(userId + ": этот id не найден");
                });
    }

    @Override
    public void deleteById(Long userId) {
        User removedUser = users.remove(userId);
        if (removedUser != null) {
            log.info("Пользователь удален: {}", removedUser.toString());
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    private Optional<User> isEmailExist(String email) {
        return users.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }
}
