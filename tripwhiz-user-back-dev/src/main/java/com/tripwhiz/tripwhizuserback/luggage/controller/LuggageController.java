package com.tripwhiz.tripwhizuserback.luggage.controller;

import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageDTO;
import com.tripwhiz.tripwhizuserback.luggage.service.LuggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/luggage")
public class LuggageController {

    @Autowired
    private LuggageService luggageService;

    @PostMapping("/saveLuggage")
    public String saveLuggage(@RequestBody LuggageDTO luggageDTO) {
        luggageService.saveLuggage(luggageDTO);
        return "Luggage saved successfully for user: " + luggageDTO.getEmail();
    }

    @GetMapping("/viewQRCode/{type}/{qrname}")
    public ResponseEntity<Resource> viewQRCode(@PathVariable String type, @PathVariable String qrname) {
        return luggageService.fetchQRCode(type, qrname);
    }
}
