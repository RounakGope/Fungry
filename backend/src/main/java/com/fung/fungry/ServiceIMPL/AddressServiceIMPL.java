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

import java.util.ArrayList;
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
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such user Found"));
        Address address=addressRepository.findById(addressId).orElseThrow(()->new RuntimeException("No Such address Found"));
        if (!address.getUser().getUserId().equals(userId))
            throw new RuntimeException("You Cant Delete Address");

        user.getAddressList().remove(address);

        addressRepository.delete(address);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, Long userId, AddressDTO addressDTO) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such user"));
        Address address=addressRepository.findById(addressId).orElseThrow(()->new RuntimeException("No such Address"));
        if (!address.getUser().getUserId().equals(userId))
            throw new RuntimeException("You cannot update address");


        address.setAddress(addressDTO.getAddress());
        address.setState(addressDTO.getState());
        address.setLandmark(addressDTO.getLandmark());
        address.setHouseNumber(addressDTO.getHouseNumber());
        address.setZipcode(addressDTO.getZipcode());
        addressRepository.save(address);
        return mapToDTO(address);

    }

    @Override
    public AddressDTO getAddressByAddressId(Long addressId, Long userId) {

        return null;
    }

    @Override
    @Transactional
    public List<AddressDTO> getAddressByUserId(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such User Found "));
        List<Address> addressList=user.getAddressList();
        List<AddressDTO> addressDTOS=new ArrayList<>();
        for (Address address:addressList)
        {
            addressDTOS.add(mapToDTO(address));
        }
        return addressDTOS;
    }
}
