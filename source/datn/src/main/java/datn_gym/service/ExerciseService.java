package datn_gym.service;

import datn_gym.dto.request.ExerciseRequest;
import datn_gym.dto.response.ExerciseResponse;
import datn_gym.entity.Exercise;
import datn_gym.entity.User;
import datn_gym.repository.ExerciseRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public List<ExerciseResponse> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .filter(ex -> ex.getIsActive() == null || ex.getIsActive())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExerciseResponse createExercise(String email, ExerciseRequest request) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        Exercise exercise = new Exercise();
        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setDescription(request.getDescription());
        exercise.setVideoUrl(request.getVideoUrl());
        exercise.setCreatedBy(author);

        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public ExerciseResponse updateExercise(Integer id, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài tập"));

        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setDescription(request.getDescription());
        exercise.setVideoUrl(request.getVideoUrl());

        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public void deleteExercise(Integer id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài tập"));
        exercise.setIsActive(false);
        exerciseRepository.save(exercise);
    }

    private ExerciseResponse toResponse(Exercise ex) {
        return ExerciseResponse.builder()
                .id(ex.getId())
                .name(ex.getName())
                .muscleGroup(ex.getMuscleGroup())
                .description(ex.getDescription())
                .videoUrl(ex.getVideoUrl())
                .createdBy(ex.getCreatedBy().getFullName())
                .build();
    }
}
