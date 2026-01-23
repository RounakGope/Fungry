package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.Address;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.AddressCreateDTO;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.Repository.AddressRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.AddressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AddressServiceIMPL implements AddressService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    public AddressDTO mapToDTO(Address address)
    {
        AddressDTO addressDTO=new AddressDTO();
        addressDTO.setAddressId(address.getAddressId());
        addressDTO.setState(address.getAddress());
        addressDTO.setAddress(address.getAddress());
        addressDTO.setLandmark(address.getLandmark());
        addressDTO.setZipcode(address.getZipcode());
        addressDTO.setHouseNumber(address.getHouseNumber());
        return addressDTO;
    }

    @Override
    @Transactional
    public AddressDTO createAddress(AddressCreateDTO address, Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such User Present"));
        Address addressToAdd=new Address();
        addressToAdd.setAddress(address.getAddress());
        addressToAdd.setState(address.getState());
        addressToAdd.setUser(user);
        addressToAdd.setLandmark(address.getLandMark());
        addressToAdd.setZipcode(address.getZipCode());
        addressToAdd.setHouseNumber(address.getHouseNumber());
        user.getAddressList().add(addressToAdd);

        userRepository.save(user);
        return mapToDTO(addressToAdd);

    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {

    }

    @Override
    public AddressDTO updateAddress(Long addressId, Long userId, AddressDTO addressDTO) {
        return null;
    }

    @Override
    public AddressDTO getAddressByAddressId(Long addressId, Long userId) {
        return null;
    }

    @Override
    public List<AddressDTO> getAddressByUserId(Long userId) {
        return List.of();
    }
}
