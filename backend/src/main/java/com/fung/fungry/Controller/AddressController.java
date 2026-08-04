package com.fung.fungry.Controller;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.ModelDTO.AddressCreateDTO;
import com.fung.fungry.ModelDTO.AddressDTO;
import com.fung.fungry.ServiceIMPL.AddressServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v2.0/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressServiceIMPL addressServiceIMPL;

    //Create Address
    @PostMapping("/")
    public ResponseEntity<AddressDTO> create(
                                             @RequestBody AddressCreateDTO address,@AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();

        AddressDTO addressDTO = addressServiceIMPL.createAddress(address, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressDTO);
    }

    // Delete Address
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Long addressId,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();
        addressServiceIMPL.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }

    // Update Address
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> update(@PathVariable Long addressId,

                                             @RequestBody AddressDTO addressDTO,@AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();
        AddressDTO updated = addressServiceIMPL.updateAddress(addressId, userId, addressDTO);
        return ResponseEntity.ok(updated);
    }

    // Get Single Address
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getById(@PathVariable Long addressId,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();
        AddressDTO addressDTO = addressServiceIMPL.getAddressByAddressId(addressId, userId);
        return ResponseEntity.ok(addressDTO);
    }

    // Get All Addresses of User
    @GetMapping("/user")
    public ResponseEntity<List<AddressDTO>> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();

        List<AddressDTO> addressList = addressServiceIMPL.getAddressByUserId(userId);
        return ResponseEntity.ok(addressList);
    }
}