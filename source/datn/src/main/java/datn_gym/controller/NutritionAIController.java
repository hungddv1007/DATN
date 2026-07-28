package datn_gym.controller;

import datn_gym.dto.request.NutritionAnalysisRequest;
import datn_gym.dto.response.NutritionAnalysisResponse;
import datn_gym.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionAIController {

    private final GeminiService geminiService;

    @PostMapping("/analyze")
    @PreAuthorize("hasRole('PT')")
    public ResponseEntity<NutritionAnalysisResponse> analyzeNutrition(@RequestBody NutritionAnalysisRequest request) {
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

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
