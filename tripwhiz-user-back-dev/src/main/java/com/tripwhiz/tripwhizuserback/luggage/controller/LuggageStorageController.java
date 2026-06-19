package com.tripwhiz.tripwhizuserback.luggage.controller;

import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageStorageDTO;
import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageStorage;
import com.tripwhiz.tripwhizuserback.luggage.service.LuggageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/luggagestorage")
@RequiredArgsConstructor
public class LuggageStorageController {

    private final LuggageStorageService luggageStorageService;

    @PostMapping("/create")
    public ResponseEntity<LuggageStorageDTO> createLuggageStorage(@RequestBody LuggageStorage luggageStorage) {
        LuggageStorageDTO response = luggageStorageService.createLuggageStorage(luggageStorage);
        return ResponseEntity.ok(response);
    }
}
