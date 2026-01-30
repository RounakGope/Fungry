package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Model.Cart;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.UserCreateDTO;
import com.fung.fungry.ModelDTO.UserDTO;
import com.fung.fungry.Repository.CartRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CartRepository cartRepository;

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
    public UserDTO addUser(@Valid UserCreateDTO userDTO) {
        Optional<User> user=userRepository.findByEmail(userDTO.getUserEmail());
        if (user.isPresent())
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email Already Present"
            );
        User newUser=new User();
        newUser.setUserMail(userDTO.getUserEmail());
        Cart cart=new Cart();
        cart.setUser(newUser);
        newUser.setCart(cart);
        newUser.setUserName(userDTO.getUserName());
        newUser.setUserPasswordHash(passwordEncoder.encode(userDTO.getPassword()));//hash password to be set
        newUser.setPhoneNumber(userDTO.getPHNO());
        newUser.setRole(UserRole.CUSTOMER);
        newUser.setCreatedAt(LocalDateTime.now());


        userRepository.save(newUser);
        return mapToDTO(newUser);


    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such user found"));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user =userRepository.findById(userId).orElseThrow(()->new RuntimeException("No User Found"));
        Optional<User> existUser=userRepository.findByEmail(userDTO.getUserEmail());
        if (existUser.isPresent() && !existUser.get().getUserId().equals(user.getUserId()))
        {
            throw new RuntimeException("Email Already Present");
        }
        user.setUserMail(userDTO.getUserEmail());
        user.setUserName(userDTO.getUserName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user =userRepository.findById(userId).orElseThrow(()->new RuntimeException("No User Found"));
        userRepository.delete(user);


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
    @Transactional
    public UserDTO updatePassword(Long userId, String oldPassword, String newPassword) {
        User user= userRepository.findById(userId).orElseThrow(()-> new RuntimeException("No such user Found"));
        if (!passwordEncoder.matches(oldPassword,user.getUserPasswordHash()))
        {
            throw new RuntimeException("Old Password Is Incorrect");
        }
        if (passwordEncoder.matches(newPassword,user.getUserPasswordHash()))
        {
            throw new RuntimeException("Old Password and new Password are same");
        }

        user.setUserPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return mapToDTO(user);
    }

    @Override
    public List<OrderDTO> viewOrderHistory(Long userId, Integer page, Integer size, String sortBy, String direction) {


        return List.of();
    }
}
