package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.user.UpdateUserRequest;
import com.arthuurdp.e_commerce.domain.dtos.user.UserResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import com.arthuurdp.e_commerce.services.mappers.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

        if (req.firstName() != null) {
            user.setFirstName(req.firstName());
        }
        if (req.lastName() != null) {
            user.setLastName(req.lastName());
        }
        if (req.phone() != null) {
            user.setPhone(req.phone());
        }
        if (req.gender() != null) {
            user.setGender(req.gender());
        }

        return mapper.toResponse(repo.save(user));
    }

    public void delete(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
}

