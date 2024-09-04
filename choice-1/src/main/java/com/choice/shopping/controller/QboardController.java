package com.choice.shopping.controller;

import com.choice.shopping.entity.Qboard;
import com.choice.shopping.entity.QboardImg;
import com.choice.shopping.service.QboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/qboard")
public class QboardController {

    @Autowired
    private QboardService qboardService;

    @PostMapping
    public ResponseEntity<Qboard> createQboard(
            @RequestPart("qboard") Qboard qboard,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Qboard createdQboard = qboardService.createQboard(qboard, userDetails.getUsername(), images);
        return new ResponseEntity<>(createdQboard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Qboard> getQboard(@PathVariable Long id) {
        Qboard qboard = qboardService.getQboard(id);
        return ResponseEntity.ok(qboard);
    }

    @GetMapping
    public ResponseEntity<List<Qboard>> getAllQboards() {
        List<Qboard> qboards = qboardService.getAllQboards();
        return ResponseEntity.ok(qboards);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Qboard> updateQboard(
            @PathVariable Long id,
            @RequestPart("qboard") Qboard qboardDetails,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "deletedImageIds", required = false) List<Long> deletedImageIds,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Qboard updatedQboard = qboardService.updateQboard(id, qboardDetails, userDetails.getUsername(), newImages,
                deletedImageIds);
        return ResponseEntity.ok(updatedQboard);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQboard(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        qboardService.deleteQboard(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{qboardId}/images")
    public ResponseEntity<List<QboardImg>> getImagesForQboard(@PathVariable Long qboardId) {
        List<QboardImg> images = qboardService.getImagesForQboard(qboardId);
        return ResponseEntity.ok(images);
    }
}