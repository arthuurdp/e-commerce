package com.arthuurdp.e_commerce.modules.user;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.user.dtos.UserResponse;
import com.arthuurdp.e_commerce.shared.storage.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/me/profile-picture")
public class UserProfilePictureController {
    private final UserService service;
    private final FileStorageService fileStorageService;

    public UserProfilePictureController(UserService service, FileStorageService fileStorageService) {
        this.service = service;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        String baseUrl = "http://192.168.0.113:8080";

        String fileName = fileStorageService.storeFile(file);
        String imageUrl = baseUrl + "/uploads/" + fileName;

        return ResponseEntity.ok().body(service.updateProfilePicture(imageUrl, authenticatedUser.getUser()));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> deleteProfilePicture(
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok().body(service.deleteProfilePicture(authenticatedUser.getUser()));
    }
}
