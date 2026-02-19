package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.user.UpdateUserRequest;
import com.arthuurdp.e_commerce.entities.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repo;
    private final EntityMapperService entityMapperService;

    public UserService(UserRepository repo, EntityMapperService entityMapperService) {
        this.repo = repo;
        this.entityMapperService = entityMapperService;
    }

    public UserResponse findById(Long id) {
        User user = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return entityMapperService.toUserResponse(user);
    }

    public Page<UserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable).map(entityMapperService::toUserResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (req.firstName() != null) {
            user.setFirstName(req.firstName());
        }
        if (req.lastName() != null) {
            user.setLastName(req.lastName());
        }
        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (!repo.existsByEmail(req.email())) {
                user.setEmail(req.email());
            } else {
                throw new ConflictException("Email already in use");
            }
        }
        return entityMapperService.toUserResponse(repo.save(user));
    }

    public void delete(Long id) {
        User user = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        repo.delete(user);
    }
}
