package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.user.UpdateUserRequest;
import com.arthuurdp.e_commerce.domain.dtos.user.UserResponse;
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
    private final UserRepository userRepository;
    private final EntityMapperService entityMapperService;

    public UserService(UserRepository userRepository, EntityMapperService entityMapperService) {
        this.userRepository = userRepository;
        this.entityMapperService = entityMapperService;
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return entityMapperService.toUserResponse(user);
    }

    public Page<UserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(entityMapperService::toUserResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (req.firstName() != null) {
            user.setFirstName(req.firstName());
        }
        if (req.lastName() != null) {
            user.setLastName(req.lastName());
        }

        if (req.email() != null) {
            if (req.email().isBlank()) {
                throw new BadRequestException("Email cannot be blank");
            }
            if (!req.email().equals(user.getEmail())) {
                if (userRepository.existsByEmail(req.email())) {
                    throw new ConflictException("Email already in use");
                }
                user.setEmail(req.email());
            }
        }

        if (req.cpf() != null) {
            if (!req.cpf().equals(user.getCpf()) && userRepository.existsByCpf(req.cpf())) {
                throw new ConflictException("CPF already in use");
            }
            user.setCpf(req.cpf());
        }

        if (req.phone() != null) {
            user.setPhone(req.phone());
        }
        if (req.birthDate() != null) {
            user.setBirthDate(req.birthDate());
        }
        if (req.gender() != null) {
            user.setGender(req.gender());
        }
        return entityMapperService.toUserResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
}

