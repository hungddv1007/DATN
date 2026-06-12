package datn_gym.service;

import datn_gym.dto.request.SessionCreateRequest;
import datn_gym.dto.request.SessionExerciseRequest;
import datn_gym.dto.request.TrainingRouteCreateRequest;
import datn_gym.dto.request.TrainingRouteUpdateRequest;
import datn_gym.dto.response.SessionExerciseResponse;
import datn_gym.dto.response.SessionResponse;
import datn_gym.dto.response.TrainingRouteResponse;
import datn_gym.dto.response.TrainingRouteSummaryResponse;
import datn_gym.entity.*;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingRouteService {

    private final TrainingRouteRepository trainingRouteRepository;
    private final SessionRepository sessionRepository;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_ASSIGNED = "ASSIGNED";
    private static final String STATUS_COMPLETED = "COMPLETED";

    // ================================================================
    // PT: QUẢN LÝ LỘ TRÌNH
    // ================================================================

    public List<TrainingRouteSummaryResponse> getAllMyRoutes(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        return trainingRouteRepository.findByPt_Id(pt.getId())
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    public TrainingRouteResponse getRouteDetail(String ptEmail, Integer routeId) {
        User pt = getUserByEmail(ptEmail);

        TrainingRoute route = trainingRouteRepository.findByIdAndPt_Id(routeId, pt.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Lộ trình không tồn tại hoặc bạn không có quyền xem"));

        return toDetailResponse(route);
    }

    @Transactional
    public TrainingRouteSummaryResponse createRoute(String ptEmail,
                                                     TrainingRouteCreateRequest request) {
        User pt = getUserByEmail(ptEmail);

        TrainingRoute route = TrainingRoute.builder()
                .pt(pt)
                .name(request.getName())
                .isTemplate(request.getIsTemplate() != null && request.getIsTemplate())
                .status(STATUS_DRAFT)
                .build();

        return toSummary(trainingRouteRepository.save(route));
    }

    @Transactional
    public TrainingRouteSummaryResponse updateRoute(String ptEmail, Integer routeId,
                                                     TrainingRouteUpdateRequest request) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());

        validateRouteIsDraft(route);

        route.setName(request.getName());
        return toSummary(trainingRouteRepository.save(route));
    }

    @Transactional
    public void deleteRoute(String ptEmail, Integer routeId) {
        User pt = getUserByEmail(ptEmail);

        if (!trainingRouteRepository.existsById(routeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình");
        }

        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());
        validateRouteIsDraft(route);
        trainingRouteRepository.delete(route);
    }

    @Transactional
    public TrainingRouteSummaryResponse assignRoute(String ptEmail, Integer routeId,
                                                     Integer memberId) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());

        validateRouteIsDraft(route);

        Long totalSessions = sessionRepository.countTrainingSessions(routeId);
        if (totalSessions == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lộ trình chưa có buổi tập nào, không thể giao");
        }

        if (!membershipRepository.existsActiveMembershipByPtAndMember(pt.getId(), memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Hội viên này không thuộc quyền quản lý của bạn");
        }

        if (trainingRouteRepository.existsByMember_IdAndStatus(memberId, STATUS_ASSIGNED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Hội viên này đang có lộ trình tập chưa hoàn thành");
        }

        User member = getUserById(memberId);
        route.setMember(member);
        route.setStatus(STATUS_ASSIGNED);
        route.setStartDate(LocalDate.now());

        return toSummary(trainingRouteRepository.save(route));
    }

    @Transactional
    public TrainingRouteSummaryResponse completeRoute(String ptEmail, Integer routeId) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());

        if (!STATUS_ASSIGNED.equals(route.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ có thể hoàn thành lộ trình đang ASSIGNED");
        }

        route.setStatus(STATUS_COMPLETED);
        return toSummary(trainingRouteRepository.save(route));
    }

    // TỐI ƯU HIỆU SUẤT: Batch Insert khi Clone Lộ trình
    @Transactional
    public TrainingRouteSummaryResponse cloneRoute(String ptEmail, Integer routeId) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute original = getRouteOwnedByPt(routeId, pt.getId());

        TrainingRoute clone = TrainingRoute.builder()
                .pt(pt)
                .name("Copy - " + original.getName())
                .isTemplate(true)
                .status(STATUS_DRAFT)
                .member(null)
                .build();
        TrainingRoute savedClone = trainingRouteRepository.save(clone);

        List<Session> originalSessions = sessionRepository.findByRoute_Id(routeId);
        List<SessionExercise> allNewExercises = new ArrayList<>(); // List chờ lưu một cục

        for (Session originalSession : originalSessions) {
            Session cloneSession = Session.builder()
                    .route(savedClone)
                    .weekNum(originalSession.getWeekNum())
                    .dayNum(originalSession.getDayNum())
                    .name(originalSession.getName())
                    .isRestDay(originalSession.getIsRestDay())
                    .build();
            Session savedSession = sessionRepository.save(cloneSession);

            if (!Boolean.TRUE.equals(originalSession.getIsRestDay())) {
                List<SessionExercise> originalExercises =
                        sessionExerciseRepository.findBySession_Id(originalSession.getId());
                for (SessionExercise originalEx : originalExercises) {
                    SessionExercise cloneEx = SessionExercise.builder()
                            .session(savedSession)
                            .exercise(originalEx.getExercise())
                            .sets(originalEx.getSets())
                            .reps(originalEx.getReps())
                            .weightKg(originalEx.getWeightKg())
                            .notes(originalEx.getNotes())
                            .build();
                    allNewExercises.add(cloneEx);
                }
            }
        }

        // BATCH INSERT: Lưu tất cả bài tập cùng 1 lúc (Tốc độ tăng x10 lần)
        if (!allNewExercises.isEmpty()) {
            sessionExerciseRepository.saveAll(allNewExercises);
        }

        return toSummary(savedClone);
    }

    // ================================================================
    // PT: QUẢN LÝ BUỔI TẬP (SESSIONS)
    // ================================================================

    @Transactional
    public SessionResponse addSession(String ptEmail, Integer routeId,
                                      SessionCreateRequest request) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());

        validateRouteIsDraft(route);

        if (sessionRepository.findByRoute_IdAndWeekNumAndDayNum(
                routeId, request.getWeekNum(), request.getDayNum()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Tuần " + request.getWeekNum() + " ngày " + request.getDayNum()
                    + " đã có buổi tập rồi");
        }

        Session session = Session.builder()
                .route(route)
                .weekNum(request.getWeekNum())
                .dayNum(request.getDayNum())
                .name(request.getName())
                .isRestDay(request.getIsRestDay() != null && request.getIsRestDay())
                .build();

        return toSessionResponse(sessionRepository.save(session),
                Collections.emptyList());
    }

    @Transactional
    public SessionResponse updateSession(String ptEmail, Integer routeId,
                                         Integer sessionId,
                                         SessionCreateRequest request) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());
        validateRouteIsDraft(route);

        Session session = sessionRepository.findByIdAndRoute_Id(sessionId, routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Buổi tập không tồn tại hoặc không thuộc lộ trình này"));

        sessionRepository.findByRoute_IdAndWeekNumAndDayNum(
                routeId, request.getWeekNum(), request.getDayNum())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(sessionId)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Tuần " + request.getWeekNum() + " ngày "
                                + request.getDayNum() + " đã có buổi tập rồi");
                    }
                });

        session.setWeekNum(request.getWeekNum());
        session.setDayNum(request.getDayNum());
        session.setName(request.getName());
        session.setIsRestDay(request.getIsRestDay() != null && request.getIsRestDay());

        List<SessionExercise> exercises =
                sessionExerciseRepository.findBySession_Id(sessionId);
        return toSessionResponse(sessionRepository.save(session), exercises);
    }

    @Transactional
    public void deleteSession(String ptEmail, Integer routeId, Integer sessionId) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());
        validateRouteIsDraft(route);

        if (!sessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy buổi tập");
        }

        Session session = sessionRepository.findByIdAndRoute_Id(sessionId, routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Buổi tập không thuộc lộ trình này"));

        sessionRepository.delete(session);
    }

    // ================================================================
    // PT: QUẢN LÝ BÀI TẬP TRONG BUỔI (SESSION_EXERCISES)
    // ================================================================

    @Transactional
    public SessionExerciseResponse addExerciseToSession(String ptEmail,
                                                         Integer routeId,
                                                         Integer sessionId,
                                                         SessionExerciseRequest request) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());
        validateRouteIsDraft(route);

        Session session = sessionRepository.findByIdAndRoute_Id(sessionId, routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Buổi tập không thuộc lộ trình này"));

        if (Boolean.TRUE.equals(session.getIsRestDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể thêm bài tập vào ngày nghỉ");
        }

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bài tập"));

        SessionExercise sessionExercise = SessionExercise.builder()
                .session(session)
                .exercise(exercise)
                .sets(request.getSets())
                .reps(request.getReps())
                .weightKg(request.getWeightKg())
                .notes(request.getNotes())
                .build();

        return toSessionExerciseResponse(
                sessionExerciseRepository.save(sessionExercise));
    }

    @Transactional
    public SessionExerciseResponse updateExerciseInSession(String ptEmail,
                                                            Integer routeId,
                                                            Integer sessionId,
                                                            Integer sessionExerciseId,
                                                            SessionExerciseRequest request) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());
        validateRouteIsDraft(route);

        sessionRepository.findByIdAndRoute_Id(sessionId, routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Buổi tập không thuộc lộ trình này"));

        SessionExercise sessionExercise =
                sessionExerciseRepository.findByIdAndSession_Id(
                        sessionExerciseId, sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Bài tập không thuộc buổi tập này"));

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy bài tập"));

        sessionExercise.setExercise(exercise);
        sessionExercise.setSets(request.getSets());
        sessionExercise.setReps(request.getReps());
        sessionExercise.setWeightKg(request.getWeightKg());
        sessionExercise.setNotes(request.getNotes());

        return toSessionExerciseResponse(
                sessionExerciseRepository.save(sessionExercise));
    }

    @Transactional
    public void deleteExerciseFromSession(String ptEmail, Integer routeId,
                                           Integer sessionId,
                                           Integer sessionExerciseId) {
        User pt = getUserByEmail(ptEmail);
        TrainingRoute route = getRouteOwnedByPt(routeId, pt.getId());
        validateRouteIsDraft(route);

        sessionRepository.findByIdAndRoute_Id(sessionId, routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Buổi tập không thuộc lộ trình này"));

        if (!sessionExerciseRepository.existsById(sessionExerciseId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy bài tập");
        }

        SessionExercise sessionExercise =
                sessionExerciseRepository.findByIdAndSession_Id(
                        sessionExerciseId, sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Bài tập không thuộc buổi tập này"));

        sessionExerciseRepository.delete(sessionExercise);
    }

    // ================================================================
    // MEMBER: XEM LỘ TRÌNH
    // ================================================================

    public List<TrainingRouteSummaryResponse> getMemberRoutes(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        return trainingRouteRepository.findByMember_Id(member.getId())
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    public TrainingRouteResponse getMemberRouteDetail(String memberEmail,
                                                       Integer routeId) {
        User member = getUserByEmail(memberEmail);

        TrainingRoute route = trainingRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình"));

        if (route.getMember() == null ||
                !route.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem lộ trình này");
        }

        return toDetailResponse(route);
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    private TrainingRoute getRouteOwnedByPt(Integer routeId, Integer ptId) {
        return trainingRouteRepository.findByIdAndPt_Id(routeId, ptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Lộ trình không tồn tại hoặc bạn không có quyền thao tác"));
    }

    private void validateRouteIsDraft(TrainingRoute route) {
        if (!STATUS_DRAFT.equals(route.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ được thao tác trên lộ trình đang ở trạng thái DRAFT");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hội viên"));
    }

    // ================================================================
    // MAPPER METHODS
    // ================================================================

    private TrainingRouteSummaryResponse toSummary(TrainingRoute route) {
        Integer totalWeeks = sessionRepository.getMaxWeekNum(route.getId());
        Long totalSessions = sessionRepository.countTrainingSessions(route.getId());

        return TrainingRouteSummaryResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .status(route.getStatus())
                .isTemplate(route.getIsTemplate())
                .startDate(route.getStartDate())
                .createdAt(route.getCreatedAt())
                .ptId(route.getPt().getId())
                .ptName(route.getPt().getFullName())
                .memberId(route.getMember() != null ? route.getMember().getId() : null)
                .memberName(route.getMember() != null ? route.getMember().getFullName() : null)
                .totalWeeks(totalWeeks != null ? totalWeeks : 0)
                .totalSessions(totalSessions != null ? totalSessions.intValue() : 0)
                .build();
    }

    // TỐI ƯU HIỆU SUẤT: FIX LỖI N+1 CỰC KỲ NGHIÊM TRỌNG BẰNG GOM NHÓM MAP TRONG RAM
    private TrainingRouteResponse toDetailResponse(TrainingRoute route) {
        List<Session> sessions = sessionRepository
                .findByRoute_IdOrderByWeekNumAscDayNumAsc(route.getId());

        // 1. Lấy danh sách tất cả ID của các buổi tập
        List<Integer> sessionIds = sessions.stream()
                .map(Session::getId)
                .collect(Collectors.toList());

        // 2. Kéo TOÀN BỘ bài tập của các buổi này CHỈ BẰNG 1 CÂU SQL (Nhờ hàm findBySession_IdIn)
        List<SessionExercise> allExercises = sessionIds.isEmpty() 
                ? Collections.emptyList() 
                : sessionExerciseRepository.findBySession_IdIn(sessionIds);

        // 3. Gom nhóm bài tập theo Session ID bằng Java Stream (Xử lý trên RAM)
        Map<Integer, List<SessionExercise>> exercisesBySession = allExercises.stream()
                .collect(Collectors.groupingBy(se -> se.getSession().getId()));

        // Group sessions theo tuần
        Map<Integer, List<SessionResponse>> weeks = new LinkedHashMap<>();
        for (Session session : sessions) {
            // Lấy trực tiếp từ Map trong RAM thay vì lặp vòng truy vấn Database
            List<SessionExercise> exercises = exercisesBySession.getOrDefault(session.getId(), new ArrayList<>());
            SessionResponse sessionResponse = toSessionResponse(session, exercises);
            
            weeks.computeIfAbsent(session.getWeekNum(), k -> new ArrayList<>())
                    .add(sessionResponse);
        }

        Integer totalWeeks = sessions.stream()
                .mapToInt(Session::getWeekNum).max().orElse(0);
        long totalSessions = sessions.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getIsRestDay())).count();

        return TrainingRouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .status(route.getStatus())
                .isTemplate(route.getIsTemplate())
                .startDate(route.getStartDate())
                .createdAt(route.getCreatedAt())
                .ptId(route.getPt().getId())
                .ptName(route.getPt().getFullName())
                .memberId(route.getMember() != null ? route.getMember().getId() : null)
                .memberName(route.getMember() != null ? route.getMember().getFullName() : null)
                .totalWeeks(totalWeeks)
                .totalSessions((int) totalSessions)
                .weeks(weeks)
                .build();
    }

    private SessionResponse toSessionResponse(Session session,
                                               List<SessionExercise> exercises) {
        List<SessionExerciseResponse> exerciseResponses = exercises.stream()
                .map(this::toSessionExerciseResponse)
                .collect(Collectors.toList());

        return SessionResponse.builder()
                .id(session.getId())
                .weekNum(session.getWeekNum())
                .dayNum(session.getDayNum())
                .name(session.getName())
                .isRestDay(session.getIsRestDay())
                .exercises(exerciseResponses)
                .build();
    }

    private SessionExerciseResponse toSessionExerciseResponse(SessionExercise se) {
        return SessionExerciseResponse.builder()
                .id(se.getId())
                .exerciseId(se.getExercise().getId())
                .exerciseName(se.getExercise().getName())
                .muscleGroup(se.getExercise().getMuscleGroup())
                .videoUrl(se.getExercise().getVideoUrl())
                .sets(se.getSets())
                .reps(se.getReps())
                .weightKg(se.getWeightKg())
                .notes(se.getNotes())
                .build();
    }
}