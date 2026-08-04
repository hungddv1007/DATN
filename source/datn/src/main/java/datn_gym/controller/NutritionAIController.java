package datn_gym.controller;

import datn_gym.dto.request.AiDietGenerationRequest;
import datn_gym.dto.request.NutritionAnalysisRequest;
import datn_gym.dto.response.AiDietGenerationResponse;
import datn_gym.dto.response.NutritionAnalysisResponse;
import datn_gym.service.AiDietGenerationService;
import datn_gym.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionAIController {

    private final GeminiService geminiService;
    private final AiDietGenerationService aiDietGenerationService;

    @PostMapping("/analyze")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<NutritionAnalysisResponse> analyzeNutrition(
            @Valid @RequestBody NutritionAnalysisRequest request) {
        boolean allEmpty = isBlank(request.getBreakfastMeal())
                && isBlank(request.getPreworkoutMeal())
                && isBlank(request.getLunchMeal())
                && isBlank(request.getPostworkoutMeal())
                && isBlank(request.getDinnerMeal());

        if (allEmpty) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập ít nhất một bữa ăn");
        }

        NutritionAnalysisResponse response = geminiService.analyzeNutrition(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-diet")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<AiDietGenerationResponse> generateDiet(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AiDietGenerationRequest request) {
        return ResponseEntity.ok(aiDietGenerationService.generate(
                userDetails.getUsername(), request));
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
