package com.fung.fungry.Service;

import com.fung.fungry.Model.Address;
import com.fung.fungry.ModelDTO.AddressCreateDTO;
import com.fung.fungry.ModelDTO.AddressDTO;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    public AddressDTO createAddress(AddressCreateDTO address, Long userId);
    public void deleteAddress(Long addressId, Long userId);
    public AddressDTO updateAddress(Long addressId,Long userId,AddressDTO addressDTO);
    public AddressDTO getAddressByAddressId(Long addressId,Long userId);
    public List<AddressDTO> getAddressByUserId(Long userId);

}
