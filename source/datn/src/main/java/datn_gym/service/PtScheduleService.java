package datn_gym.service;

import datn_gym.dto.request.ScheduleRequest;
import datn_gym.dto.response.ScheduleSlotResponse;
import datn_gym.entity.PtSchedule;
import datn_gym.entity.User;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
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
@Transactional(readOnly = true)
public class PtScheduleService {

    private final PtScheduleRepository ptScheduleRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    // Lấy tất cả lịch kèm ACTIVE của PT
    public List<ScheduleSlotResponse> getAllByPt(String ptEmail) {
        User pt = getUserByEmail(ptEmail);
        return ptScheduleRepository.findByPtIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(pt.getId(), "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy lịch kèm của PT với 1 member cụ thể
    public List<ScheduleSlotResponse> getByPtAndMember(String ptEmail, Integer memberId) {
        User pt = getUserByEmail(ptEmail);
        return ptScheduleRepository.findByPtIdAndMemberIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(pt.getId(), memberId, "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lưu/Cập nhật lịch tập của PT cho 1 member (batch update)
    @Transactional
    public List<ScheduleSlotResponse> saveMemberSchedule(String ptEmail, ScheduleRequest request) {
        User pt = getUserByEmail(ptEmail);
        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học viên"));

        // Kiểm tra học viên có thuộc PT này không
        if (!membershipRepository.existsActiveMembershipByPtAndMember(pt.getId(), member.getId())) {
            throw new IllegalArgumentException("Học viên này không thuộc quản lý của bạn hoặc gói tập đã hết hạn!");
        }

        // Xóa các slot cũ của member này
        List<PtSchedule> existingSlots = ptScheduleRepository.findByPtIdAndMemberIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(pt.getId(), member.getId(), "ACTIVE");
        for (PtSchedule slot : existingSlots) {
            slot.setStatus("CANCELLED");
        }
        ptScheduleRepository.saveAll(existingSlots);

        // Thêm các slot mới
        if (request.getSlots() != null) {
            for (ScheduleRequest.SlotItem item : request.getSlots()) {
                if (item.getDayOfWeek() < 0 || item.getDayOfWeek() > 5) {
                    throw new IllegalArgumentException("Ngày trong tuần chỉ từ Thứ 2 (0) đến Thứ 7 (5)!");
                }
                if (item.getSlotIndex() < 0 || item.getSlotIndex() > 7) {
                    throw new IllegalArgumentException("Khung giờ không hợp lệ!");
                }

                // Kiểm tra xung đột: slot này đã có học viên khác đặt chưa
                ptScheduleRepository.findByPtIdAndDayOfWeekAndSlotIndexAndStatus(pt.getId(), item.getDayOfWeek(), item.getSlotIndex(), "ACTIVE")
                        .ifPresent(s -> {
                            if (!s.getMember().getId().equals(member.getId())) {
                                throw new IllegalArgumentException("Khung giờ thứ " + (item.getDayOfWeek() + 2) + " slot " + (item.getSlotIndex() + 1) + " đã bị trùng với học viên khác!");
                            }
                        });

                PtSchedule newSlot = PtSchedule.builder()
                        .pt(pt)
                        .member(member)
                        .dayOfWeek(item.getDayOfWeek())
                        .slotIndex(item.getSlotIndex())
                        .exerciseNote(item.getExerciseNote())
                        .status("ACTIVE")
                        .build();
                ptScheduleRepository.save(newSlot);
            }
        }

        return ptScheduleRepository.findByPtIdAndMemberIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(pt.getId(), member.getId(), "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy lịch kèm của chính member đăng nhập
    public List<ScheduleSlotResponse> getMySchedule(String memberEmail) {
        User member = getUserByEmail(memberEmail);
        return ptScheduleRepository.findByMemberIdAndStatusOrderByDayOfWeekAscSlotIndexAsc(member.getId(), "ACTIVE")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Helper: Map Entity -> DTO
    private ScheduleSlotResponse toResponse(PtSchedule s) {
        return ScheduleSlotResponse.builder()
                .id(s.getId())
                .ptId(s.getPt().getId())
                .ptName(s.getPt().getFullName())
                .memberId(s.getMember().getId())
                .memberName(s.getMember().getFullName())
                .dayOfWeek(s.getDayOfWeek())
                .slotIndex(s.getSlotIndex())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .sessionLabel(s.getSessionLabel())
                .exerciseNote(s.getExerciseNote())
                .status(s.getStatus())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }
}
