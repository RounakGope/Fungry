package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.Address;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.AddressCreateDTO;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.Repository.AddressRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.AddressService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AddressServiceIMPL implements AddressService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    private static final Logger log= LoggerFactory.getLogger(AddressServiceIMPL.class);
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
        log.info("Started creating address for userId={} ",userId);User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new RuntimeException("No Such User Present");
                });
        Address addressToAdd=new Address();
        addressToAdd.setAddress(address.getAddress());
        addressToAdd.setState(address.getState());
        addressToAdd.setUser(user);
        addressToAdd.setLandmark(address.getLandMark());
        addressToAdd.setZipcode(address.getZipCode());
        addressToAdd.setHouseNumber(address.getHouseNumber());
        log.info("created address entity for UserId {}",userId);
        user.getAddressList().add(addressToAdd);

        userRepository.save(user);
        log.info("added address entity to user {} ,address_Id ={}",userId,addressToAdd.getAddressId());
        return mapToDTO(addressToAdd);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        log.info("Attempting to delete addressId={} for userId={}",
                addressId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new RuntimeException("No Such User Present");
                });
        Address address=addressRepository.findById(addressId).orElseThrow(()->new RuntimeException("No Such address Found"));
        if (!address.getUser().getUserId().equals(userId))
        {

            log.warn("cannot delete the address for user={},with addressId={}",userId,addressId);
            throw new RuntimeException("You Cant Delete Address");

        }

        user.getAddressList().remove(address);
        log.info("Address {} deleted successfully for user {}",
                addressId, userId);

        addressRepository.delete(address);
        log.info("deleted address from database");

        userRepository.save(user);

    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, Long userId, AddressDTO addressDTO) {

        log.info("Updating addressId={} for userId={}", addressId, userId);User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new RuntimeException("No Such User Present");
                });
        Address address=addressRepository.findById(addressId).orElseThrow(()->new RuntimeException("No such Address"));
        if (!address.getUser().getUserId().equals(userId))
        {

            log.warn("User {} attempted to update address {} not belonging to them",
                    userId, addressId);
            throw new RuntimeException("You cannot update address");
        }

        address.setAddress(addressDTO.getAddress());
        address.setState(addressDTO.getState());
        address.setLandmark(addressDTO.getLandmark());
        address.setHouseNumber(addressDTO.getHouseNumber());
        address.setZipcode(addressDTO.getZipcode());
        addressRepository.save(address);
        log.info("Address {} updated successfully for user {}",
                addressId, userId);
        return mapToDTO(address);

    }

    @Override
    public AddressDTO getAddressByAddressId(Long addressId, Long userId) {

        Address address =addressRepository.findById(addressId).orElseThrow(()->new RuntimeException("No Such Address Found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new RuntimeException("No Such User Present");
                });
        if(!address.getUser().getUserId().equals(user.getUserId()))
        {
            log.warn("User{} attempted to access address {} not belonging to them",userId,addressId);
            throw new RuntimeException("Mismatch in user and Address");
        }

        return mapToDTO(address);

    }

    @Override
    @Transactional
    public List<AddressDTO> getAddressByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new RuntimeException("No Such User Present");
                });
        List<Address> addressList=user.getAddressList();
        List<AddressDTO> addressDTOS=new ArrayList<>();
        for (Address address:addressList)
        {
            addressDTOS.add(mapToDTO(address));
        }
        return addressDTOS;
    }
}
