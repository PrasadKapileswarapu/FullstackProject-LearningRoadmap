package com.roadmap.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roadmap.dto.RoadmapHistoryResponse;
import com.roadmap.dto.RoadmapRequest;
import com.roadmap.dto.RoadmapResponse;
import com.roadmap.service.RoadmapService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Both endpoints require a valid JWT. The user is identified by the token, never by a request parameter. */
@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/generate")
    public ResponseEntity<RoadmapResponse> generateRoadmap(@Valid @RequestBody RoadmapRequest request,
                                                           Authentication authentication) {
        RoadmapResponse response = roadmapService.generateRoadmap(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RoadmapHistoryResponse>> getUserHistory(Authentication authentication) {
        return ResponseEntity.ok(roadmapService.getUserHistory(authentication.getName()));
    }
}
