package com.fung.fungry.Service;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.ModelDTO.OrderDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import com.fung.fungry.ModelDTO.UserDTO;

import java.util.List;

public interface UserService {
    public UserDTO addUser(UserDTO userDTO);
    public UserDTO getUserById(Long userId);
    public UserDTO updateUser(Long userId,UserDTO userDTO);
    public void deleteUser(Long userId);//only admin can delete
    public UserRole getUserRole(Long userId);
  //  public UserDTO updateUserAddress(Long userId, AddressDTO addressDTO);
    public UserDTO updateUserPNo(Long userId,String PHno);
    public UserDTO updatePassword(Long userId,String oldPassword,String newPassword);
    public List<OrderDTO> viewOrderHistory(Long userId,
            Integer page,Integer size,String sortBy,String direction);


}
