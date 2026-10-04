package datn_gym.controller;

import datn_gym.dto.response.PolicyVersionResponse;
import datn_gym.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<PolicyVersionResponse>> getAll() {
        return ResponseEntity.ok(policyService.getAllActive());
    }

    @GetMapping("/{type}")
    public ResponseEntity<PolicyVersionResponse> getOne(@PathVariable String type) {
        return ResponseEntity.ok(policyService.getActive(type.toUpperCase()));
    }
}
