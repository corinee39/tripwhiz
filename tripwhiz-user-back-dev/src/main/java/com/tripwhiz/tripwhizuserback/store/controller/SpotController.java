package com.tripwhiz.tripwhizuserback.store.controller;


import com.tripwhiz.tripwhizuserback.store.dto.SpotDTO;
import com.tripwhiz.tripwhizuserback.store.service.SpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/spot")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    // 특정 Spot 조회
    @GetMapping("/{spno}")
    public ResponseEntity<SpotDTO> read(@PathVariable Long spno) {
        log.info("----- Spot Read Request -----");
        log.info("Spot ID to read: {}", spno);

        SpotDTO spotDTO = spotService.read(spno);

        log.info("Successfully retrieved Spot: {}", spotDTO);
        log.info("----- End of Spot Read Request -----");

        return ResponseEntity.ok(spotDTO);
    }

    // 모든 Spot 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<SpotDTO>> list() {
        log.info("Request to fetch Spot List from Admin Server");

        // 점주 서버의 Spot 리스트를 가져오는 서비스 호출
        List<SpotDTO> spotList = spotService.fetchSpotListFromAdminServer();

        if (spotList != null && !spotList.isEmpty()) {
            log.info("Successfully fetched {} spots from Admin Server", spotList.size());
        } else {
            log.warn("No spots available from Admin Server");
        }

        return ResponseEntity.ok(spotList);


    }

    @PostMapping("/sync")
    public ResponseEntity<Void> syncFromAdmin() {
        spotService.syncSpotsFromAdmin();
        return ResponseEntity.ok().build();
    }
}
