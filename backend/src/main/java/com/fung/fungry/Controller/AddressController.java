package com.fung.fungry.Controller;

import com.fung.fungry.ModelDTO.AddressCreateDTO;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.ServiceIMPL.AddressServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v2.0/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressServiceIMPL addressServiceIMPL;

    //Create Address
    @PostMapping("/{userId}")
    public ResponseEntity<AddressDTO> create(@PathVariable Long userId,
                                             @RequestBody AddressCreateDTO address) {

        AddressDTO addressDTO = addressServiceIMPL.createAddress(address, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressDTO);
    }

    // Delete Address
    @DeleteMapping("/{addressId}/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long addressId,
                                       @PathVariable Long userId) {

        addressServiceIMPL.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }

    // Update Address
    @PutMapping("/{addressId}/{userId}")
    public ResponseEntity<AddressDTO> update(@PathVariable Long addressId,
                                             @PathVariable Long userId,
                                             @RequestBody AddressDTO addressDTO) {

        AddressDTO updated = addressServiceIMPL.updateAddress(addressId, userId, addressDTO);
        return ResponseEntity.ok(updated);
    }

    // Get Single Address
    @GetMapping("/{addressId}/{userId}")
    public ResponseEntity<AddressDTO> getById(@PathVariable Long addressId,
                                              @PathVariable Long userId) {

        AddressDTO addressDTO = addressServiceIMPL.getAddressByAddressId(addressId, userId);
        return ResponseEntity.ok(addressDTO);
    }

    // Get All Addresses of User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressDTO>> getAll(@PathVariable Long userId) {

        List<AddressDTO> addressList = addressServiceIMPL.getAddressByUserId(userId);
        return ResponseEntity.ok(addressList);
    }
}