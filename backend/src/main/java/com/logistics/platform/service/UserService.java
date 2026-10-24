package com.logistics.platform.service;

import com.logistics.platform.dto.user.UpdateUserRequest;
import com.logistics.platform.dto.user.UserResponse;

public interface UserService {

    UserResponse getUserByEmail(String email);
    
    UserResponse updateUser(String email, UpdateUserRequest request);
}
