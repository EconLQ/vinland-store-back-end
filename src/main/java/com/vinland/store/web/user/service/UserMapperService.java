package com.vinland.store.web.user.service;

import com.vinland.store.web.user.model.Role;
import com.vinland.store.web.user.model.User;
import com.vinland.store.web.user.model.UserDTO;
import com.vinland.store.web.user.model.UserInfoDTO;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserMapperService {

    public UserDTO entityToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setOldPassword(user.getPassword());
        userDTO.setNewPassword(null);
        return userDTO;
    }

    public UserInfoDTO entityToUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setFirstName(user.getFirstName());
        userInfoDTO.setLastName(user.getLastName());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setRoles(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()));
        return userInfoDTO;
    }
}
