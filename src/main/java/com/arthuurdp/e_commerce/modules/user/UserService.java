package com.arthuurdp.e_commerce.modules.user;

import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.user.dtos.UpdateUserRequest;
import com.arthuurdp.e_commerce.modules.user.dtos.UserResponse;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.user.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final UserMapper mapper;

    public UserService(UserRepository repo, UserMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public UserResponse findById(Long id) {
        return mapper.toResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public Page<UserResponse> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::toResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional.ofNullable(req.firstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(req.lastName()).ifPresent(user::setLastName);
        Optional.ofNullable(req.phone()).ifPresent(user::setPhone);
        Optional.ofNullable(req.gender()).ifPresent(user::setGender);

        return mapper.toResponse(repo.save(user));
    }

    public void delete(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
}

