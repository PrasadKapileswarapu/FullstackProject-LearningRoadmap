package com.roadmap.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roadmap.dto.RoadmapRequest;
import com.roadmap.dto.RoadmapResponse;
import com.roadmap.model.UserRoadmap;
import com.roadmap.service.RoadmapService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RoadmapController {

    private final RoadmapService roadmapService;


    @PostMapping("/generate")
    public ResponseEntity<RoadmapResponse> generateRoadmap(@Valid @RequestBody RoadmapRequest request) {
        RoadmapResponse response = roadmapService.generateRoadmap(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/history")
    public ResponseEntity<List<UserRoadmap>> getUserHistory(@RequestParam String email) {
        List<UserRoadmap> history = roadmapService.getUserHistory(email);
        return ResponseEntity.ok(history); // 200 OK
    }
}
