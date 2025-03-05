package com.example.firstTry.controller;

import com.example.firstTry.dto.AuthRequest;
import com.example.firstTry.dto.DoctorRequestDTO;
import com.example.firstTry.dto.DoctorResponseDTO;
import com.example.firstTry.dto.UserResponse;
import com.example.firstTry.model.Admin;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.Patient;
import com.example.firstTry.model.User;
import com.example.firstTry.security.jwt.JwtService;
import com.example.firstTry.services.DoctorService;
import com.example.firstTry.services.FileStorageService;
import com.example.firstTry.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final DoctorService doctorService;
    private final FileStorageService fileStorageService;

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        Admin savedAdmin = userService.createAdmin(admin);
        String token = jwtService.generateToken(savedAdmin);
        return ResponseEntity.ok(Map.of("token", token));
    }


//    @PostMapping("/register/doctor")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<DoctorResponseDTO> createDoctor(
//            @Valid @RequestBody DoctorRequestDTO doctorRequest) {
//        return ResponseEntity.ok(doctorService.createDoctor(doctorRequest));
//    }

    @PostMapping(value = "/register/doctor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerDoctor(
            @Valid @RequestPart("data") DoctorRequestDTO doctorRequest,
            @RequestPart("aptitudeCert") MultipartFile aptitudeCert,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto) {

        try {
            DoctorResponseDTO response = doctorService.createDoctor(
                    doctorRequest,
                    aptitudeCert,
                    profilePhoto
            );
            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed: " + ex.getMessage()));
        }
    }

    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@RequestBody Patient patient) {
        Patient savedPatient = userService.registerPatient(patient);
        String token = jwtService.generateToken(savedPatient);
        // Changed 'user' to 'savedPatient'
        return ResponseEntity.ok(new UserResponse(savedPatient, token));
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(request.getEmail());

        if (!(userDetails instanceof User)) {
            throw new AuthenticationServiceException("Unexpected user type");
        }

        User user = (User) userDetails;

        if (!user.isEnabled()) {
            throw new DisabledException("Account not approved");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new UserResponse(user, token));
    }
}