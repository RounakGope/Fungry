package com.fung.fungry.Service;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.ModelDTO.*;

import java.util.List;

public interface UserService {
    public UserDTO addUser(UserCreateDTO userDTO);
    public UserDTO getUserById(Long userId);
    public UserDTO updateUser(Long userId,UserDTO userDTO);
    public void deleteUser(Long userId);//only admin can delete
    public UserRole getUserRole(Long userId);
  //  public UserDTO updateUserAddress(Long userId, AddressDTO addressDTO);
    public UserDTO updateUserPNo(Long userId,String PHno);
    List<UserDTO> getAllUsers(int page, int size, String direction, String sortBy, UserRole role);
    public UserDTO updatePassword(Long userId,String oldPassword,String newPassword);
    public List<OrderHistoryDTO> viewOrderHistory(Long userId,
            Integer page,Integer size,String sortBy,String direction);


}
