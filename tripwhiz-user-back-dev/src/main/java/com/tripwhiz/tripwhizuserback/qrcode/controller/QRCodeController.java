package com.tripwhiz.tripwhizuserback.qrcode.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/user/qrcode")
@RequiredArgsConstructor
public class QRCodeController {

    @Value("${com.tripwhiz.uploadBasic}")
    private String uploadBasePath;

    @Value("${com.tripwhiz.upload.qrcodepath}")
    private String qrCodePath;

    // QR 코드 보기 (사용자가 자신의 주문에 대한 QR 코드 보기)
    @GetMapping("/view/{qrname}")
    public ResponseEntity<Resource> viewQRCode(@PathVariable String qrname) {
        File qrFile = Paths.get(uploadBasePath, qrCodePath, qrname + ".png").toFile();

        if (!qrFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(qrFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    // QR 코드 보기 (보관 신청 QR 코드)
    @GetMapping("/view/storage/{requestId}")
    public ResponseEntity<Resource> viewStorageQRCode(@PathVariable Long requestId) {
        String fileName = "Storage-" + requestId + ".png";
        return getQRCodeResource(fileName);
    }

    // QR 코드 보기 (이동 신청 QR 코드)
    @GetMapping("/view/move/{requestId}")
    public ResponseEntity<Resource> viewMoveQRCode(@PathVariable Long requestId) {
        String fileName = "Move-" + requestId + ".png";
        return getQRCodeResource(fileName);
    }

    // QR 코드 파일 확인 및 반환
    private ResponseEntity<Resource> getQRCodeResource(String fileName) {
        File qrFile = Paths.get(uploadBasePath, qrCodePath, fileName).toFile();

        if (!qrFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(qrFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
