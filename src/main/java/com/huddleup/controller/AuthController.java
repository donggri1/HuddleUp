package com.huddleup.controller;

import com.huddleup.config.JwtUtil;
import com.huddleup.dto.AuthRequest;
import com.huddleup.dto.AuthResponse;
import com.huddleup.dto.SignupRequest;
import com.huddleup.dto.UserDto;
import com.huddleup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, 
                          UserDetailsService userDetailsService, 
                          JwtUtil jwtUtil,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("잘못된 사용자 이름 또는 비밀번호입니다.", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            UserDto userDto = new UserDto();
            userDto.setUsername(signupRequest.getUsername());
            userDto.setPassword(signupRequest.getPassword());
            userDto.setEmail(signupRequest.getEmail());
            userDto.setName(signupRequest.getName());
            
            UserDto createdUser = userService.createUser(userDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "사용자가 성공적으로 등록되었습니다.");
            response.put("username", createdUser.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "회원가입 실패");
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 없이 접근 가능한 테스트 엔드포인트입니다.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/secured")
    public ResponseEntity<?> secured() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "JWT 인증이 필요한 보안 엔드포인트입니다.");
        return ResponseEntity.ok(response);
    }
}