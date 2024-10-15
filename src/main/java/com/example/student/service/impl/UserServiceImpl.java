package com.example.student.service.impl;

import com.example.student.dto.request.UserDtoRequest;
import com.example.student.dto.response.UserDtoResponse;
import com.example.student.entity.Role;
import com.example.student.entity.User;
import com.example.student.enums.RoleEnum;
import com.example.student.exception.CustomException;
import com.example.student.exception.ErrorCode;
import com.example.student.mapper.UserMapper;
import com.example.student.repository.RoleRepository;
import com.example.student.repository.UserRepository;
import com.example.student.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
// dùng này thì bỏ Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j // add logger
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    @Override
    public UserDtoResponse createUser(UserDtoRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) throw new CustomException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        List<Role> roles= roleRepository.findAllById(request.getRole());
        if(CollectionUtils.isEmpty(roles)) throw new CustomException(ErrorCode.ROLE_NOT_EXISTED);
        var role= new HashSet<Role>();
        roles.forEach(r-> role.add(r));
        user.setRoles(role);
        userRepository.save(user);
        return userMapper.toUserDtoResponse( user );
    }

    @Override
    public UserDtoResponse update(UserDtoRequest request, Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_EXISTED));
        if(userRepository.existsByUsername(request.getUsername()) && !userRepository.existsById(id)) throw new CustomException(ErrorCode.USER_EXISTED);
        userMapper.updateUser(user,request);
        PasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var role= roleRepository.findAllById(request.getRole());
        user.setRoles(new HashSet<>(role));
        return userMapper.toUserDtoResponse(userRepository.save(user));
    }

    //@PreAuthorize("hasRole('ADMIN')")//nó check role trước khi chạy vào function
   // @PreAuthorize("hasAuthority('ROLE_ADMIN')") // bỏ prefix ROLE => ai có permision thì được vô hàm này
    @Override
    public List<UserDtoResponse> getAllUser() {
        List<User> users= userRepository.findAll();
        return userMapper.toUserDtoResponseList(users);
    }

    @PostAuthorize("returnObject.username==authentication.name")// vào function thực thi code rồi mới check role
    @Override
    public UserDtoResponse getUser(Long id) {
        User user= userRepository.findById(id).orElseThrow(()-> new CustomException(ErrorCode.USER_EXISTED));
        return userMapper.toUserDtoResponse(user);
    }

    @Override
    public UserDtoResponse myInfo() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user= userRepository.findByUsername(authentication.getName( )).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserDtoResponse(user);
    } 
}
