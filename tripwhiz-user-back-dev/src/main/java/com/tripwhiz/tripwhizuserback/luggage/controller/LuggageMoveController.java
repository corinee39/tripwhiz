package com.tripwhiz.tripwhizuserback.luggage.controller;

import com.tripwhiz.tripwhizuserback.luggage.dto.LuggageMoveDTO;
import com.tripwhiz.tripwhizuserback.luggage.entity.LuggageMove;
import com.tripwhiz.tripwhizuserback.luggage.service.LuggageMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/luggagemove")
@RequiredArgsConstructor
public class LuggageMoveController {

    private final LuggageMoveService luggageMoveService;

    @PostMapping("/create")
    public ResponseEntity<LuggageMoveDTO> createLuggageMove(@RequestBody LuggageMove luggageMove) {
        LuggageMoveDTO response = luggageMoveService.createLuggageMove(luggageMove);
        return ResponseEntity.ok(response);
    }
}
