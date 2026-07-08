package datn_gym.service;

import datn_gym.dto.request.PtScheduleRequest;
import datn_gym.dto.response.PtScheduleResponse;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.User;
import datn_gym.entity.PtProfile;
import datn_gym.repository.PtScheduleRepository;
import datn_gym.repository.UserRepository;
import datn_gym.repository.PtProfileRepository;
import datn_gym.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PtScheduleService {

    private final PtScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PtProfileRepository ptProfileRepository;
    private final MembershipRepository membershipRepository;

    // Lấy tất cả lịch của PT đang đăng nhập
    public List<PtScheduleResponse> getSchedulesByPt(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        return scheduleRepository.findByPt_IdAndStatus(pt.getId(), "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy lịch của 1 member cụ thể do PT quản lý
    public List<PtScheduleResponse> getSchedulesByPtAndMember(String ptEmail, Integer memberId) {
        User pt = getUserByEmail(ptEmail);
        return scheduleRepository.findByPt_IdAndMember_IdAndStatus(pt.getId(), memberId, "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Member xem lịch của mình
    public List<PtScheduleResponse> getMySchedules(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        return scheduleRepository.findByMember_IdAndStatus(member.getId(), "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // PT tạo lịch (batch/single)
    @Transactional
    public PtScheduleResponse createSchedule(String ptEmail, PtScheduleRequest request) {
        User pt = getUserByEmail(ptEmail);
        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học viên"));

        // Validate time_slot
        if (!request.getTimeSlot().matches("^(MORNING|AFTERNOON|EVENING)$")) {
            throw new IllegalArgumentException("Slot giờ chỉ có thể là MORNING, AFTERNOON, hoặc EVENING");
        }

        // Validate PT Profile limit
        PtProfile ptProfile = ptProfileRepository.findByUser_Id(pt.getId())
                .orElseThrow(() -> new IllegalArgumentException("PT chưa có profile"));
                
        // Check nếu PT đã full member (đếm số member khác nhau trong lịch ACTIVE)
        List<PtSchedule> activeSchedules = scheduleRepository.findByPt_IdAndStatus(pt.getId(), "ACTIVE");
        long currentMembers = activeSchedules.stream().map(s -> s.getMember().getId()).distinct().count();
        
        // Nếu member này chưa có trong ds lịch của PT, thì tính là thêm mới
        boolean isNewMember = activeSchedules.stream().noneMatch(s -> s.getMember().getId().equals(member.getId()));
        
        if (isNewMember && currentMembers >= ptProfile.getMaxMembers()) {
            throw new IllegalArgumentException("Bạn đã đạt giới hạn tối đa " + ptProfile.getMaxMembers() + " học viên!");
        }

        // Kiểm tra xem member này có đang có gói ACTIVE và PT này không
        boolean hasValidMembership = membershipRepository.findByUser_IdAndStatus(member.getId(), "ACTIVE")
                .map(m -> m.getPt() != null && m.getPt().getId().equals(pt.getId()))
                .orElse(false);
        if (!hasValidMembership) {
            throw new IllegalArgumentException("Học viên này chưa đăng ký hoặc không thuộc quản lý của bạn.");
        }

        // Check xung đột slot (PT không được xếp 2 người cùng 1 slot)
        Optional<PtSchedule> conflict = scheduleRepository.findByPt_IdAndDayOfWeekAndTimeSlotAndStatus(
                pt.getId(), request.getDayOfWeek(), request.getTimeSlot(), "ACTIVE");
        if (conflict.isPresent() && !conflict.get().getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Bạn đã có lịch với học viên khác (" + conflict.get().getMember().getFullName() + ") vào slot này!");
        }

        // Nếu đã có slot này với chính member đó rồi thì trả về luôn (Idempotent)
        if (conflict.isPresent() && conflict.get().getMember().getId().equals(member.getId())) {
            return toResponse(conflict.get());
        }

        PtSchedule schedule = PtSchedule.builder()
                .pt(pt)
                .member(member)
                .dayOfWeek(request.getDayOfWeek())
                .timeSlot(request.getTimeSlot())
                .status("ACTIVE")
                .build();
        
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(String ptEmail, Integer scheduleId) {
        User pt = getUserByEmail(ptEmail);
        PtSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch"));

        if (!schedule.getPt().getId().equals(pt.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xóa lịch này");
        }

        schedule.setStatus("CANCELLED");
        scheduleRepository.save(schedule);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    private PtScheduleResponse toResponse(PtSchedule s) {
        return PtScheduleResponse.builder()
                .id(s.getId())
                .ptId(s.getPt().getId())
                .ptName(s.getPt().getFullName())
                .memberId(s.getMember().getId())
                .memberName(s.getMember().getFullName())
                .memberAvatar(s.getMember().getAvatar())
                .dayOfWeek(s.getDayOfWeek())
                .timeSlot(s.getTimeSlot())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
