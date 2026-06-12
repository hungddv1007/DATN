package datn_gym.service;

import datn_gym.dto.request.DietCreateRequest;
import datn_gym.dto.request.DietUpdateRequest;
import datn_gym.dto.response.DietResponse;
import datn_gym.entity.Diet;
import datn_gym.entity.User;
import datn_gym.repository.DietRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietService {

        private final DietRepository dietRepository;
        private final UserRepository userRepository;
        private final MembershipRepository membershipRepository;

        // ================================================================
        // PT: QUẢN LÝ THỰC ĐƠN
        // ================================================================

        public List<DietResponse> getDietsByMember(String ptEmail, Integer memberId) {
                User pt = getUserByEmail(ptEmail);
                validatePtCanManageDiet(pt.getId(), memberId);
                return dietRepository.findByPt_IdAndMember_IdOrderByDateDesc(pt.getId(), memberId)
                                .stream().map(this::toResponse).collect(Collectors.toList());
        }

        public DietResponse getDietByMemberAndDate(String ptEmail, Integer memberId,
                        LocalDate date) {
                User pt = getUserByEmail(ptEmail);
                validatePtCanManageDiet(pt.getId(), memberId);

                return dietRepository.findByPt_IdAndMember_IdAndDate(pt.getId(), memberId, date)
                                .map(this::toResponse)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Không tìm thấy thực đơn ngày " + date));
        }

        @Transactional
        public DietResponse createDiet(String ptEmail, DietCreateRequest request) {
                User pt = getUserByEmail(ptEmail);

                validatePtCanManageDiet(pt.getId(), request.getMemberId());
                validateAtLeastOneMeal(request.getBreakfast(), request.getLunch(), request.getDinner());

                if (dietRepository.existsByMember_IdAndDate(request.getMemberId(), request.getDate())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                        "Đã có thực đơn cho ngày " + request.getDate()
                                                        + ". Vui lòng sửa thực đơn cũ.");
                }

                User member = getUserById(request.getMemberId());

                Diet diet = Diet.builder()
                                .pt(pt).member(member)
                                .date(request.getDate())
                                .breakfast(request.getBreakfast())
                                .lunch(request.getLunch())
                                .dinner(request.getDinner())
                                .build();

                return toResponse(dietRepository.save(diet));
        }

        @Transactional
        public DietResponse updateDiet(String ptEmail, Integer dietId, DietUpdateRequest request) {
                User pt = getUserByEmail(ptEmail);

                // TỐI ƯU HIỆU SUẤT: Chỉ gọi DB 1 lần (findBy ID) rồi dùng Java (RAM) check
                // quyền PT
                Diet diet = dietRepository.findById(dietId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Không tìm thấy thực đơn"));

                if (!diet.getPt().getId().equals(pt.getId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN, "Bạn không có quyền sửa thực đơn này");
                }

                validateAtLeastOneMeal(request.getBreakfast(), request.getLunch(), request.getDinner());

                diet.setBreakfast(request.getBreakfast());
                diet.setLunch(request.getLunch());
                diet.setDinner(request.getDinner());

                return toResponse(dietRepository.save(diet));
        }

        @Transactional
        public void deleteDiet(String ptEmail, Integer dietId) {
                User pt = getUserByEmail(ptEmail);

                // TỐI ƯU HIỆU SUẤT: Chỉ gọi DB 1 lần (findBy ID) rồi dùng Java (RAM) check
                // quyền PT
                Diet diet = dietRepository.findById(dietId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Không tìm thấy thực đơn"));

                if (!diet.getPt().getId().equals(pt.getId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN, "Bạn không có quyền xóa thực đơn này");
                }

                dietRepository.delete(diet);
        }

        // ================================================================
        // MEMBER: XEM THỰC ĐƠN
        // ================================================================

        public List<DietResponse> getMyDiets(String memberEmail) {
                User member = getUserByEmail(memberEmail);

                // FIX NGHIỆP VỤ: Đã xóa validateMemberIsVip()
                // Cho phép hội viên (dù đã hết hạn gói VIP) vẫn được xem lại lịch sử các thực
                // đơn cũ

                return dietRepository.findByMember_IdOrderByDateDesc(member.getId())
                                .stream().map(this::toResponse).collect(Collectors.toList());
        }

        public DietResponse getMyDietByDate(String memberEmail, LocalDate date) {
                User member = getUserByEmail(memberEmail);

                // FIX NGHIỆP VỤ: Đã xóa validateMemberIsVip()

                return dietRepository.findByMember_IdAndDate(member.getId(), date)
                                .map(this::toResponse)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Không có thực đơn cho ngày " + date));
        }

        public List<DietResponse> getMyDietsByWeek(String memberEmail,
                        LocalDate fromDate, LocalDate toDate) {
                User member = getUserByEmail(memberEmail);

                // FIX NGHIỆP VỤ: Đã xóa validateMemberIsVip()

                if (fromDate.isAfter(toDate)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Ngày bắt đầu phải trước ngày kết thúc");
                }
                if (toDate.toEpochDay() - fromDate.toEpochDay() > 31) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Khoảng thời gian xem tối đa là 31 ngày");
                }

                return dietRepository.findByMember_IdAndDateBetweenOrderByDateAsc(
                                member.getId(), fromDate, toDate)
                                .stream().map(this::toResponse).collect(Collectors.toList());
        }

        // ================================================================
        // HELPER METHODS
        // ================================================================

        private void validatePtCanManageDiet(Integer ptId, Integer memberId) {
                if (!membershipRepository.existsVipMembershipByPtAndMember(ptId, memberId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "Hội viên này không thuộc quyền quản lý của bạn "
                                                        + "hoặc không có gói VIP");
                }
        }

        // Hàm validateMemberIsVip() đã được loại bỏ do không còn sử dụng

        private void validateAtLeastOneMeal(String breakfast,
                        String lunch, String dinner) {
                boolean allEmpty = (breakfast == null || breakfast.isBlank())
                                && (lunch == null || lunch.isBlank())
                                && (dinner == null || dinner.isBlank());
                if (allEmpty) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Thực đơn phải có ít nhất 1 bữa ăn");
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

        private DietResponse toResponse(Diet diet) {
                return DietResponse.builder()
                                .id(diet.getId())
                                .date(diet.getDate())
                                .memberId(diet.getMember().getId())
                                .memberName(diet.getMember().getFullName())
                                .ptId(diet.getPt().getId())
                                .ptName(diet.getPt().getFullName())
                                .breakfast(diet.getBreakfast())
                                .lunch(diet.getLunch())
                                .dinner(diet.getDinner())
                                .build();
        }
}