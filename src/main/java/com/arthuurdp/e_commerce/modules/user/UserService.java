package com.arthuurdp.e_commerce.modules.user;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.user.dtos.UpdateUserRequest;
import com.arthuurdp.e_commerce.modules.user.dtos.UserResponse;
import com.arthuurdp.e_commerce.shared.exceptions.AccessDeniedException;
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

    public UserResponse findById(Long id, User user) {
        if (!user.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        return mapper.toResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public UserResponse findCurrentUser(User user) {
        return mapper.toResponse(repo.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public Page<UserResponse> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::toResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req, User user) {
        if (!user.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        User targetUser = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        updateUserHelper(req, targetUser);

        return mapper.toResponse(repo.save(targetUser));
    }

    @Transactional
    public UserResponse updateCurrentUser(UpdateUserRequest req, User user) {
        User targetUser = repo.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        updateUserHelper(req, targetUser);

        return mapper.toResponse(repo.save(targetUser));
    }

    public void delete(Long id, User user) {
        if (!user.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public void deleteCurrentUser(User user) {
        repo.delete(repo.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    private void updateUserHelper(UpdateUserRequest req, User targetUser) {
        Optional.ofNullable(req.firstName()).ifPresent(targetUser::setFirstName);
        Optional.ofNullable(req.lastName()).ifPresent(targetUser::setLastName);
        Optional.ofNullable(req.phone()).ifPresent(targetUser::setPhone);
        Optional.ofNullable(req.gender()).ifPresent(targetUser::setGender);
    }
}

