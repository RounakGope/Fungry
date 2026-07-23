package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Model.Cart;
import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.OrderHistoryDTO;
import com.fung.fungry.ModelDTO.UserCreateDTO;
import com.fung.fungry.ModelDTO.UserDTO;
import com.fung.fungry.Repository.OrderRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceIMPL implements UserService {
   private static final Logger log = LoggerFactory.getLogger(UserServiceIMPL.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final  OrderRepository orderRepository;

    public UserDTO mapToDTO(User user)
    {
        UserDTO userDTO= new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setUserId(user.getUserId());
        userDTO.setUserEmail(user.getUserMail());
        userDTO.setUserRole(user.getRole());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        return userDTO;
    }
    public OrderHistoryDTO mapToOrderDTO(Order order)
    {
        OrderHistoryDTO orderDTO=new OrderHistoryDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setCreatedTime(order.getCreatedAt());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalAmt(order.getAmount());
        orderDTO.setRestaurantName(order.getRestaurant().getName());
        return orderDTO;
    }

    @Override
    @Transactional
    public UserDTO addUser(@Valid UserCreateDTO userDTO) {

        Optional<User> user=userRepository.findByUserMail(userDTO.getUserEmail());
        if (user.isPresent()) {
            log.warn("User already present with username={}", userDTO.getUserName());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email Already Present"
            );
        }
            User newUser=new User();
        newUser.setUserMail(userDTO.getUserEmail());
        Cart cart=new Cart();
        cart.setTotalAmt(0L);
        cart.setUser(newUser);

        newUser.setCart(cart);
        newUser.setUserName(userDTO.getUserName());
        newUser.setUserPasswordHash(passwordEncoder.encode(userDTO.getPassword()));//hash password to be set
        newUser.setPhoneNumber(userDTO.getPhoneNumber());
        newUser.setRole(UserRole.CUSTOMER);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setIsActive(true);

        userRepository.save(newUser);
        log.info("created user with userId={}",newUser.getUserId());
        return mapToDTO(newUser);
    }


    @Override
    public UserDTO getUserById(Long userId) {
        log.info("Fetching user with id={}", userId);
        User user =userRepository.findById(userId).orElseThrow(()->{
            log.warn("no such user found with userid={}",userId);
            return new RuntimeException("No User Found");
        });
        return mapToDTO(user);
    }


    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        log.info("started update user with user id={}",userId);
        User user =userRepository.findById(userId).orElseThrow(()->{
            log.warn("no such user found with userid={}",userId);
            return new RuntimeException("No User Found");
        });
        Optional<User> existUser=userRepository.findByUserMail(userDTO.getUserEmail());
        if (existUser.isPresent() && !existUser.get().getUserId().equals(user.getUserId()))
        {
            log.warn("User already present with username={}", userDTO.getUserName());
            throw new RuntimeException("Email Already Present");
        }
        user.setUserMail(userDTO.getUserEmail());
        user.setUserName(userDTO.getUserName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getUserRole());
        user.setUpdatedAt(LocalDateTime.now());
        log.info("updated the user with userId={}",userId);
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deactivating user with id={}", userId);
        User user =userRepository.findById(userId).orElseThrow(()->{
            log.warn("no such user found with userid={}",userId);
            return new RuntimeException("No User Found");
        });
        user.setIsActive(false);
        userRepository.save(user);
        log
                .info("user deactivated successfully with user id={}",userId);
    }


    @Override
    public UserRole getUserRole(Long userId) {
        log.info("Fetching role for user {}", userId);
        User user =userRepository.findById(userId).orElseThrow(()->{
            log.warn("no such user found with userid={}",userId);
            return new RuntimeException("No User Found");
        });
        UserRole role= user.getRole();
        return role;
    }

    @Override
    public UserDTO updateUserPNo(Long userId, String PHno) {
        log.info("Updating phone number for user {}", userId);
        User user =userRepository.findById(userId).orElseThrow(()->{
            log.warn("no such user found with userid={}",userId);
            return new RuntimeException("No User Found");
        });
       user.setPhoneNumber(PHno);
       userRepository.save(user);
       log.info("updated user Phone no with userId={}",userId);
       return mapToDTO(user);

    }

    @Override
    public List<UserDTO> getAllUsers(int page, int size, String direction, String sortBy, UserRole role) {
        log.info("Fetching all users page={} size={} role={}", page, size, role);
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = (role != null)
                ? userRepository.findByRole(role, pageable)
                : userRepository.findAll(pageable);

        return userPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public UserDTO updatePassword(Long userId, String oldPassword, String newPassword) {
        log.info("User {} requested password change", userId);
        User user =userRepository.findById(userId).orElseThrow(()->{
            log.warn("no such user found with userid={}",userId);
            return new RuntimeException("No User Found");
        });
        if (!passwordEncoder.matches(oldPassword,user.getUserPasswordHash()))
        {
            log.warn("User {} entered incorrect old password", userId);
            throw new RuntimeException("Old Password Is Incorrect");
        }
        if (passwordEncoder.matches(newPassword,user.getUserPasswordHash()))
        {
            log.warn("User {} attempted to reuse old password", userId);
            throw new RuntimeException("Old Password and new Password are same");
        }

        user.setUserPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user {}", userId);

        return mapToDTO(user);
    }

    @Override
    @Transactional
    public List<OrderHistoryDTO> viewOrderHistory(Long userId, Integer page, Integer size, String sortBy, String direction) {
        User user =userRepository.findById(userId).orElseThrow(()->{
        log.warn("no such user found with userid={}",userId);
        return new RuntimeException("No User Found");
    });
        Sort sort="descending".equalsIgnoreCase(direction)?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        log.info("Fetching order history for user {} page={} size={}", userId, page, size);
        Pageable pageable=PageRequest.of(page,size,sort);
        Page<Order> orderPage=orderRepository.findByUser(user,pageable);
        return orderPage.getContent()
                .stream().map(this::mapToOrderDTO)
                .toList();
    }
}
