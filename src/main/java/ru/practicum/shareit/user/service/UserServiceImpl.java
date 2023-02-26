package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.exception.UserServiceException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto dto) {
        if (StringUtils.isBlank(dto.getEmail()) || StringUtils.isBlank(dto.getName())) {
            throw new UserServiceException("поля name и/или description не заполнены");
        }
        return userMapper.toDto(repository.create(userMapper.toUser(dto)));
    }

    @Override
    public UserDto update(Long userId, UserDto dto) {
        User user = userMapper.toUser(dto);
        user.setId(userId);
        return userMapper.toDto(repository.update(user));
    }

    @Override
    public UserDto getById(Long userId) {
        return userMapper.toDto(repository.getById(userId));
    }

    @Override
    public void deleteById(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return userMapper.toUserDtos(repository.getAll());
    }
}
