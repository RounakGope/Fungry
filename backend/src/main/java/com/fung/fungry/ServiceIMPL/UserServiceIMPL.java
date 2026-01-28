package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.UserDTO;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    UserRepository userRepository;

    public UserDTO mapToDTO(User user)
    {
        UserDTO userDTO= new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setUserId(user.getUserId());
        userDTO.setUserRole(user.getRole());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        return userDTO;
    }
    @Override
    @Transactional
    public UserDTO addUser(UserCreateDTO userDTO) {


    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such user found"));
        return mapToDTO(user);
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public UserRole getUserRole(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("No such user Found"));
        UserRole role= user.getRole();
        return role;
    }

    @Override
    public UserDTO updateUserPNo(Long userId, String PHno) {
        User user= userRepository.findById(userId).orElseThrow(()-> new RuntimeException("No such user Found"));
       user.setPhoneNumber(PHno);
       userRepository.save(user);
       return mapToDTO(user);

    }

    @Override
    public UserDTO updatePassword(Long userId, String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public List<OrderDTO> viewOrderHistory(Long userId, Integer page, Integer size, String sortBy, String direction) {


        return List.of();
    }
}
