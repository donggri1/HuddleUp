package com.huddleup.service;

import com.huddleup.dto.UserDto;
import com.huddleup.entity.User;
import com.huddleup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        // 사용자 이름 중복 확인
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("이미 사용 중인 사용자 이름입니다.");
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 엔티티 생성
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.addRole("ROLE_USER"); // 기본 역할 부여

        // 데이터베이스에 저장
        User savedUser = userRepository.save(user);

        // DTO로 변환하여 반환
        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }

    @Transactional
    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(user -> {
                    // 이메일 변경 시 중복 확인
                    if (!user.getEmail().equals(userDto.getEmail()) && 
                            userRepository.existsByEmail(userDto.getEmail())) {
                        throw new RuntimeException("이미 사용 중인 이메일입니다.");
                    }

                    // 비밀번호가 제공된 경우에만 업데이트
                    if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    }

                    user.setEmail(userDto.getEmail());
                    user.setName(userDto.getName());
                    
                    return convertToDto(userRepository.save(user));
                });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 엔티티를 DTO로 변환
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRoles(user.getRoles());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
} 