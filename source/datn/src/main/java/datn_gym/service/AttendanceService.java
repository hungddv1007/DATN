package datn_gym.service;

import datn_gym.dto.response.AttendanceResponse;
import datn_gym.dto.response.AttendanceSummaryResponse;
import datn_gym.entity.Attendance;
import datn_gym.entity.Session;
import datn_gym.entity.TrainingRoute;
import datn_gym.entity.User;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final TrainingRouteRepository trainingRouteRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    private static final String STATUS_ASSIGNED = "ASSIGNED";

    // ================================================================
    // MEMBER: ĐIỂM DANH
    // ================================================================

    @Transactional
    public AttendanceResponse checkIn(String memberEmail, Integer sessionId) {
        User member = getUserByEmail(memberEmail);

        // FIX LazyInitializationException: dùng findWithRouteById
        // Load session + route + route.pt + route.member trong 1 SQL
        Session session = sessionRepository.findWithRouteById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy buổi tập"));

        // REVIEW 1: Không điểm danh ngày nghỉ
        if (Boolean.TRUE.equals(session.getIsRestDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể điểm danh ngày nghỉ");
        }

        TrainingRoute route = session.getRoute();

        // REVIEW 2: Lộ trình phải đang ASSIGNED
        if (!STATUS_ASSIGNED.equals(route.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lộ trình chưa được kích hoạt hoặc đã kết thúc");
        }

        // REVIEW 3 — FIX IDOR: Buổi tập phải thuộc lộ trình của chính member này
        if (route.getMember() == null ||
                !route.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Buổi tập này không thuộc lộ trình của bạn");
        }

        // REVIEW 4: Mỗi buổi chỉ điểm danh 1 lần
        if (attendanceRepository.existsByMember_IdAndSession_Id(
                member.getId(), sessionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bạn đã điểm danh buổi tập này rồi");
        }

        Attendance attendance = Attendance.builder()
                .member(member)
                .session(session)
                .status(true)
                .build();

        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional
    public void cancelCheckIn(String memberEmail, Integer attendanceId) {
        User member = getUserByEmail(memberEmail);

        // FIX: Phân biệt 404 vs 403
        if (!attendanceRepository.existsById(attendanceId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy điểm danh");
        }

        // FIX IDOR: 1 câu SQL vừa tìm vừa check ownership
        Attendance attendance = attendanceRepository
                .findByIdAndMember_Id(attendanceId, member.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Bạn không có quyền hủy điểm danh này"));

        // REVIEW 5: Không hủy điểm danh của lộ trình đã COMPLETED
        if (!STATUS_ASSIGNED.equals(attendance.getSession().getRoute().getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể hủy điểm danh của lộ trình đã kết thúc");
        }

        attendanceRepository.delete(attendance);
    }

    // FIX LazyInitializationException: thêm @Transactional(readOnly=true)
    // cho tất cả method READ có truy cập LAZY field
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendanceByRoute(String memberEmail,
                                                            Integer routeId) {
        User member = getUserByEmail(memberEmail);

        TrainingRoute route = trainingRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình"));

        if (route.getMember() == null ||
                !route.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Lộ trình này không thuộc về bạn");
        }

        return attendanceRepository.findByMemberAndRoute(member.getId(), routeId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getMyAttendanceSummary(String memberEmail,
                                                             Integer routeId) {
        User member = getUserByEmail(memberEmail);

        TrainingRoute route = trainingRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình"));

        if (route.getMember() == null ||
                !route.getMember().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Lộ trình này không thuộc về bạn");
        }

        return buildSummary(route, member.getId());
    }

    // ================================================================
    // PT: XEM ĐIỂM DANH HỘI VIÊN
    // ================================================================

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMemberAttendanceByRoute(String ptEmail,
                                                                Integer memberId,
                                                                Integer routeId) {
        User pt = getUserByEmail(ptEmail);
        validatePtOwnsMember(pt.getId(), memberId);
        validateRouteOwnedByMember(routeId, memberId);

        return attendanceRepository.findByMemberAndRoute(memberId, routeId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getMemberAttendanceSummary(String ptEmail,
                                                                 Integer memberId,
                                                                 Integer routeId) {
        User pt = getUserByEmail(ptEmail);
        validatePtOwnsMember(pt.getId(), memberId);

        TrainingRoute route = validateRouteOwnedByMember(routeId, memberId);
        return buildSummary(route, memberId);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceBySession(String ptEmail,
                                                            Integer sessionId) {
        User pt = getUserByEmail(ptEmail);

        // FIX LazyInitializationException: dùng findWithRouteById
        Session session = sessionRepository.findWithRouteById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy buổi tập"));

        // REVIEW 9: Buổi tập phải thuộc lộ trình của PT này
        if (!session.getRoute().getPt().getId().equals(pt.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Buổi tập này không thuộc lộ trình của bạn");
        }

        return attendanceRepository.findBySession_Id(sessionId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    private void validatePtOwnsMember(Integer ptId, Integer memberId) {
        if (!membershipRepository.existsActiveMembershipByPtAndMember(ptId, memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Hội viên này không thuộc quyền quản lý của bạn");
        }
    }

    // Tách ra method riêng để tái sử dụng — tránh duplicate code
    private TrainingRoute validateRouteOwnedByMember(Integer routeId, Integer memberId) {
        TrainingRoute route = trainingRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy lộ trình"));

        if (route.getMember() == null ||
                !route.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Lộ trình này không thuộc về hội viên đã chọn");
        }
        return route;
    }

    // Tách buildSummary — dùng chung cho cả Member và PT
    private AttendanceSummaryResponse buildSummary(TrainingRoute route,
                                                    Integer memberId) {
        Long totalSessions = sessionRepository
                .countTrainingSessions(route.getId());
        Long presentSessions = attendanceRepository
                .countPresentSessions(memberId, route.getId());
        Long absentSessions = totalSessions - presentSessions;

        double attendanceRate = totalSessions > 0
                ? Math.round((presentSessions * 100.0 / totalSessions) * 10.0) / 10.0
                : 0.0;

        return AttendanceSummaryResponse.builder()
                .routeId(route.getId())
                .routeName(route.getName())
                .totalSessions(totalSessions)
                .presentSessions(presentSessions)
                .absentSessions(absentSessions)
                .attendanceRate(attendanceRate)
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // FIX N+1: @EntityGraph đã load member, session, route sẵn trong Repository
    private AttendanceResponse toResponse(Attendance attendance) {
        Session session = attendance.getSession();
        TrainingRoute route = session.getRoute();

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .memberId(attendance.getMember().getId())
                .memberName(attendance.getMember().getFullName())
                .sessionId(session.getId())
                .weekNum(session.getWeekNum())
                .dayNum(session.getDayNum())
                .sessionName(session.getName())
                .isRestDay(session.getIsRestDay())
                .routeId(route.getId())
                .routeName(route.getName())
                .status(attendance.getStatus())
                .checkInTime(attendance.getCheckInTime())
                .build();
    }
}
