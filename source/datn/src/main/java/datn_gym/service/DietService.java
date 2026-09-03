package datn_gym.service;

import datn_gym.dto.request.DietCreateRequest;
import datn_gym.dto.request.DietUpdateRequest;
import datn_gym.dto.response.DietResponse;
import datn_gym.entity.Diet;
import datn_gym.entity.User;
import datn_gym.repository.DietRepository;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.PtScheduleRepository;
import datn_gym.repository.UserRepository;
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
public class DietService {

    private final DietRepository dietRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MembershipRepository membershipRepository;
    private final PtScheduleRepository ptScheduleRepository;

        // Các giá trị dayType hợp lệ
        private static final String TRAINING_DAY = "TRAINING_DAY";
        private static final String REST_DAY = "REST_DAY";
        private static final String SPECIFIC_DATE = "SPECIFIC_DATE";
        private static final Set<String> VALID_DAY_TYPES = Set.of(TRAINING_DAY, REST_DAY, SPECIFIC_DATE);
        private static final List<String> TRAINING_SCHEDULE_STATUSES = List.of("SCHEDULED", "COMPLETED");

        // ================================================================
        // PT: QUẢN LÝ THỰC ĐƠN
        // ================================================================

        /** PT xem tất cả diet đã tạo cho 1 member */
        public List<DietResponse> getDietsByMember(String ptEmail, Integer memberId) {
                User pt = userService.getUserByEmail(ptEmail);
                validatePtCanManageDiet(pt.getId(), memberId);
                return dietRepository.findByPt_IdAndMember_IdOrderByCreatedAtDesc(pt.getId(), memberId)
                                .stream().map(this::toResponse).collect(Collectors.toList());
        }

        /** PT xem mẫu TRAINING_DAY hoặc REST_DAY của 1 member */
        public DietResponse getDietTemplate(String ptEmail, Integer memberId, String dayType) {
                User pt = userService.getUserByEmail(ptEmail);
                validatePtCanManageDiet(pt.getId(), memberId);
                validateDayType(dayType);

                return dietRepository.findByPt_IdAndMember_IdAndDayType(pt.getId(), memberId, dayType)
                                .map(this::toResponse)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Chưa có mẫu thực đơn " + dayType + " cho hội viên này"));
        }

        /** PT tạo mẫu thực đơn mới (TRAINING_DAY / REST_DAY / SPECIFIC_DATE) */
        @Transactional
        public DietResponse createDiet(String ptEmail, DietCreateRequest request) {
                User pt = userService.getUserByEmail(ptEmail);
                validatePtCanManageDiet(pt.getId(), request.getMemberId());
                validateDayType(request.getDayType());
                validateAtLeastOneMeal(request.getBreakfast(), request.getSnackMorning(),
                                request.getLunch(), request.getSnackAfternoon(), request.getDinner());

                // Validate nghiệp vụ
                if (SPECIFIC_DATE.equals(request.getDayType())) {
                        if (request.getDietDate() == null) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Loại SPECIFIC_DATE phải có ngày cụ thể");
                        }
                        // Kiểm tra trùng ngày
                        if (dietRepository.existsByMember_IdAndDayTypeAndDietDate(
                                        request.getMemberId(), SPECIFIC_DATE, request.getDietDate())) {
                                throw new ResponseStatusException(HttpStatus.CONFLICT,
                                                "Đã có thực đơn cho ngày " + request.getDietDate());
                        }
                } else {
                        // TRAINING_DAY / REST_DAY: Chỉ cho phép 1 mẫu mỗi loại
                        if (dietRepository.existsByPt_IdAndMember_IdAndDayType(
                                        pt.getId(), request.getMemberId(), request.getDayType())) {
                                throw new ResponseStatusException(HttpStatus.CONFLICT,
                                                "Đã có mẫu " + request.getDayType()
                                                                + ". Hãy sửa mẫu cũ thay vì tạo mới.");
                        }
                }

                User member = getUserById(request.getMemberId());

                Diet diet = Diet.builder()
                                .pt(pt)
                                .member(member)
                                .dayType(request.getDayType())
                                .dietDate(request.getDietDate())
                                .title(request.getTitle())
                                .breakfast(request.getBreakfast())
                                .snackMorning(request.getSnackMorning())
                                .lunch(request.getLunch())
                                .snackAfternoon(request.getSnackAfternoon())
                                .dinner(request.getDinner())
                                .calories(request.getCalories() != null ? request.getCalories() : 0)
                                .proteinG(request.getProteinG() != null ? request.getProteinG() : 0)
                                .carbsG(request.getCarbsG() != null ? request.getCarbsG() : 0)
                                .fatG(request.getFatG() != null ? request.getFatG() : 0)
                                .note(request.getNote())
                                .build();

                return toResponse(dietRepository.save(diet));
        }

        /** PT sửa thực đơn (cả mẫu lẫn SPECIFIC_DATE) */
        @Transactional
        public DietResponse updateDiet(String ptEmail, Integer dietId, DietUpdateRequest request) {
                User pt = userService.getUserByEmail(ptEmail);

                Diet diet = dietRepository.findById(dietId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Không tìm thấy thực đơn"));

                if (!diet.getPt().getId().equals(pt.getId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN, "Bạn không có quyền sửa thực đơn này");
                }

                validateAtLeastOneMeal(request.getBreakfast(), request.getSnackMorning(),
                                request.getLunch(), request.getSnackAfternoon(), request.getDinner());

                diet.setTitle(request.getTitle());
                diet.setBreakfast(request.getBreakfast());
                diet.setSnackMorning(request.getSnackMorning());
                diet.setLunch(request.getLunch());
                diet.setSnackAfternoon(request.getSnackAfternoon());
                diet.setDinner(request.getDinner());
                diet.setCalories(request.getCalories() != null ? request.getCalories() : 0);
                diet.setProteinG(request.getProteinG() != null ? request.getProteinG() : 0);
                diet.setCarbsG(request.getCarbsG() != null ? request.getCarbsG() : 0);
                diet.setFatG(request.getFatG() != null ? request.getFatG() : 0);
                diet.setNote(request.getNote());

                return toResponse(dietRepository.save(diet));
        }

        /** PT xóa thực đơn */
        @Transactional
        public void deleteDiet(String ptEmail, Integer dietId) {
                User pt = userService.getUserByEmail(ptEmail);

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
        // MEMBER: XEM THỰC ĐƠN + AUTO-MAPPING
        // ================================================================

        /** Member xem toàn bộ thực đơn (mẫu + specific) */
        public List<DietResponse> getMyDiets(String memberEmail) {
                User member = userService.getUserByEmail(memberEmail);
                return dietRepository.findByMember_IdOrderByCreatedAtDesc(member.getId())
                                .stream().map(this::toResponse).collect(Collectors.toList());
        }

        /**
         * Member xem thực đơn của 1 ngày cụ thể
         * AUTO-MAPPING LOGIC:
         *   1. Kiểm tra SPECIFIC_DATE trước (ưu tiên cao nhất)
         *   2. Nếu không có → Check pt_schedules ngày đó
         *      - Có lịch tập → Load mẫu TRAINING_DAY
         *      - Không có → Load mẫu REST_DAY
         */
        public DietResponse getMyDietForDate(String memberEmail, LocalDate date) {
                User member = userService.getUserByEmail(memberEmail);

                // Bước 1: Ưu tiên SPECIFIC_DATE
                Optional<Diet> specificDiet = dietRepository
                                .findByMember_IdAndDayTypeAndDietDate(member.getId(), SPECIFIC_DATE, date);
                if (specificDiet.isPresent()) {
                        DietResponse resp = toResponse(specificDiet.get());
                        resp.setIsTrainingDay(isTrainingDay(member.getId(), date));
                        return resp;
                }

                // Bước 2: Auto-mapping theo lịch tập
                boolean hasTraining = isTrainingDay(member.getId(), date);
                String targetDayType = hasTraining ? TRAINING_DAY : REST_DAY;

                Optional<Diet> templateDiet = dietRepository
                                .findByMember_IdAndDayType(member.getId(), targetDayType);
                if (templateDiet.isPresent()) {
                        DietResponse resp = toResponse(templateDiet.get());
                        resp.setDietDate(date); // Gán ngày hiện tại để frontend hiển thị đúng
                        resp.setIsTrainingDay(hasTraining);
                        return resp;
                }

                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Chưa có thực đơn cho ngày " + date
                                                + ". PT chưa tạo mẫu " + targetDayType + ".");
        }

        /**
         * Member xem thực đơn tuần — trả về list 7 ngày, mỗi ngày kèm badge TRAINING/REST
         */
        public List<DietResponse> getMyDietWeekView(String memberEmail,
                        LocalDate fromDate, LocalDate toDate) {
                User member = userService.getUserByEmail(memberEmail);

                if (fromDate.isAfter(toDate)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Ngày bắt đầu phải trước ngày kết thúc");
                }
                if (toDate.toEpochDay() - fromDate.toEpochDay() > 31) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Khoảng thời gian xem tối đa là 31 ngày");
                }

                // Load mẫu 1 lần
                Optional<Diet> trainingTemplate = dietRepository
                                .findByMember_IdAndDayType(member.getId(), TRAINING_DAY);
                Optional<Diet> restTemplate = dietRepository
                                .findByMember_IdAndDayType(member.getId(), REST_DAY);

                List<DietResponse> weekView = new ArrayList<>();
                LocalDate current = fromDate;

                while (!current.isAfter(toDate)) {
                        final LocalDate checkDate = current;

                        // Ưu tiên SPECIFIC_DATE
                        Optional<Diet> specificDiet = dietRepository
                                        .findByMember_IdAndDayTypeAndDietDate(
                                                        member.getId(), SPECIFIC_DATE, checkDate);

                        boolean hasTraining = isTrainingDay(member.getId(), checkDate);

                        if (specificDiet.isPresent()) {
                                DietResponse resp = toResponse(specificDiet.get());
                                resp.setIsTrainingDay(hasTraining);
                                weekView.add(resp);
                        } else {
                                // Auto-mapping
                                Optional<Diet> template = hasTraining ? trainingTemplate : restTemplate;
                                if (template.isPresent()) {
                                        DietResponse resp = toResponse(template.get());
                                        resp.setDietDate(checkDate);
                                        resp.setIsTrainingDay(hasTraining);
                                        weekView.add(resp);
                                } else {
                                        // Ngày không có thực đơn → trả placeholder
                                        weekView.add(DietResponse.builder()
                                                        .dietDate(checkDate)
                                                        .isTrainingDay(hasTraining)
                                                        .dayType(hasTraining ? TRAINING_DAY : REST_DAY)
                                                        .build());
                                }
                        }

                        current = current.plusDays(1);
                }

                return weekView;
        }

        // ================================================================
        // HELPER METHODS
        // ================================================================

        /** Kiểm tra ngày có lịch tập không — dùng pt_schedules */
        private boolean isTrainingDay(Integer memberId, LocalDate date) {
                var schedules = ptScheduleRepository
                                .findByMemberIdAndScheduleDateBetweenAndStatusInOrderByScheduleDateAscStartTimeAsc(
                                                memberId, date, date, TRAINING_SCHEDULE_STATUSES);
                return schedules != null && !schedules.isEmpty();
        }

        private void validatePtCanManageDiet(Integer ptId, Integer memberId) {
                if (!membershipRepository.existsVipMembershipByPtAndMember(ptId, memberId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "Hội viên này không thuộc quyền quản lý của bạn "
                                                        + "hoặc không có gói VIP");
                }
        }

        private void validateDayType(String dayType) {
                if (dayType == null || !VALID_DAY_TYPES.contains(dayType)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "dayType phải là TRAINING_DAY, REST_DAY hoặc SPECIFIC_DATE");
                }
        }

        private void validateAtLeastOneMeal(String breakfast, String snackMorning,
                        String lunch, String snackAfternoon, String dinner) {
                boolean allEmpty = isBlank(breakfast) && isBlank(snackMorning)
                                && isBlank(lunch) && isBlank(snackAfternoon)
                                && isBlank(dinner);
                if (allEmpty) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Thực đơn phải có ít nhất 1 bữa ăn");
                }
        }

        private boolean isBlank(String s) {
                return s == null || s.isBlank();
        }



        private User getUserById(Integer id) {
                return userRepository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Không tìm thấy hội viên"));
        }

        private DietResponse toResponse(Diet diet) {
                return DietResponse.builder()
                                .id(diet.getId())
                                .dayType(diet.getDayType())
                                .dietDate(diet.getDietDate())
                                .title(diet.getTitle())
                                .memberId(diet.getMember().getId())
                                .memberName(diet.getMember().getFullName())
                                .ptId(diet.getPt().getId())
                                .ptName(diet.getPt().getFullName())
                                .breakfast(diet.getBreakfast())
                                .snackMorning(diet.getSnackMorning())
                                .lunch(diet.getLunch())
                                .snackAfternoon(diet.getSnackAfternoon())
                                .dinner(diet.getDinner())
                                .calories(diet.getCalories())
                                .proteinG(diet.getProteinG())
                                .carbsG(diet.getCarbsG())
                                .fatG(diet.getFatG())
                                .note(diet.getNote())
                                .build();
        }
}
