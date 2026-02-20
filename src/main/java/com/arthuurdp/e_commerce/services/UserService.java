package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.user.UpdateUserRequest;
import com.arthuurdp.e_commerce.entities.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
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
        boolean updated = false;

        if (req.firstName() != null) {
            user.setFirstName(req.firstName());
            updated = true;
        }

        if (req.lastName() != null) {
            user.setLastName(req.lastName());
            updated = true;
        }

        if (req.email() != null) {
            if (req.email().isBlank()) {
                throw new BadRequestException("Email cannot be blank");
            }
            if (!req.email().equals(user.getEmail())) {
                if (repo.existsByEmail(req.email())) {
                    throw new ConflictException("Email already in use");
                }
                user.setEmail(req.email());
                updated = true;
            }
        }

        if (!updated) {
            throw new BadRequestException("No valid fields provided");
        }

        return entityMapperService.toUserResponse(repo.save(user));
    }

    public void delete(Long id) {
        User user = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        repo.delete(user);
    }
}
