package datn_gym.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import datn_gym.ai.AiClient;
import datn_gym.dto.request.AiDietGenerationRequest;
import datn_gym.dto.response.AiDietGenerationResponse;
import datn_gym.entity.MemberProfile;
import datn_gym.entity.User;
import datn_gym.repository.MemberProfileRepository;
import datn_gym.repository.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiDietGenerationServiceTest {

    @Mock
    private AiClient aiClient;
    @Mock
    private UserService userService;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private MemberProfileRepository memberProfileRepository;
    @Mock
    private AiRateLimitService rateLimitService;

    private AiDietGenerationService service;

    @BeforeEach
    void setUp() {
        service = new AiDietGenerationService(
                new ObjectMapper(),
                aiClient,
                userService,
                membershipRepository,
                memberProfileRepository,
                rateLimitService);
    }

    @Test
    void insufficientProfileIsRejectedBeforeCallingAi() {
        User pt = User.builder().id(10).email("pt@gym.local").build();
        MemberProfile profile = MemberProfile.builder()
                .weightKg(new BigDecimal("70"))
                .build();
        AiDietGenerationRequest request = request(20, "TRAINING_DAY");

        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(membershipRepository.existsVipMembershipByPtAndMember(10, 20))
                .thenReturn(true);
        when(memberProfileRepository.findByUser_Id(20))
                .thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> service.generate(pt.getEmail(), request))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> {
                            assertThat(exception.getStatusCode())
                                    .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                            assertThat(exception.getReason())
                                    .contains("mức độ vận động", "mục tiêu tập luyện");
                        });

        verifyNoInteractions(aiClient, rateLimitService);
    }

    @Test
    void assignedPtCanGenerateTrainingDayDietFromPhysicalProfile() {
        User pt = User.builder().id(10).email("pt@gym.local").build();
        MemberProfile profile = MemberProfile.builder()
                .weightKg(new BigDecimal("70"))
                .heightCm(new BigDecimal("172"))
                .activityLevel("MODERATE")
                .fitnessGoal("MUSCLE_GAIN")
                .injuryHistory("Đau đầu gối nhẹ")
                .build();
        AiDietGenerationRequest request = request(20, "TRAINING_DAY");

        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(membershipRepository.existsVipMembershipByPtAndMember(10, 20))
                .thenReturn(true);
        when(memberProfileRepository.findByUser_Id(20))
                .thenReturn(Optional.of(profile));
        when(aiClient.generateStructuredJson(
                org.mockito.ArgumentMatchers.anyString(), anyMap()))
                .thenReturn("""
                        {
                          "title": "Thực đơn tăng cơ ngày tập",
                          "breakfast": "Yến mạch 60g và 2 quả trứng",
                          "snackMorning": "Một quả chuối trước buổi tập",
                          "lunch": "Cơm 200g và ức gà 180g",
                          "snackAfternoon": "Sữa chua sau buổi tập",
                          "dinner": "Khoai lang 200g và cá 180g",
                          "note": "Uống đủ nước và điều chỉnh khẩu phần cùng PT."
                        }
                        """);

        AiDietGenerationResponse response =
                service.generate(pt.getEmail(), request);

        assertThat(response.getTitle()).isEqualTo("Thực đơn tăng cơ ngày tập");
        assertThat(response.getSnackMorning()).contains("chuối");
        assertThat(response.getDinner()).contains("cá");
        verify(rateLimitService).checkAndRecord(10);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiClient).generateStructuredJson(promptCaptor.capture(), anyMap());
        assertThat(promptCaptor.getValue())
                .contains("Cân nặng hiện tại: 70 kg")
                .contains("Mục tiêu tập luyện: Tăng cơ")
                .contains("Tiền sử chấn thương: Đau đầu gối nhẹ")
                .contains("Không phân tích calo hoặc chất dinh dưỡng");
    }

    @Test
    void unassignedPtCannotUseMemberProfileForAiGeneration() {
        User pt = User.builder().id(10).email("pt@gym.local").build();
        AiDietGenerationRequest request = request(20, "REST_DAY");
        when(userService.getUserByEmail(pt.getEmail())).thenReturn(pt);
        when(membershipRepository.existsVipMembershipByPtAndMember(10, 20))
                .thenReturn(false);

        assertThatThrownBy(() -> service.generate(pt.getEmail(), request))
                .isInstanceOfSatisfying(
                        ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));

        verify(memberProfileRepository, never()).findByUser_Id(20);
        verifyNoInteractions(aiClient, rateLimitService);
    }

    private AiDietGenerationRequest request(Integer memberId, String dayType) {
        AiDietGenerationRequest request = new AiDietGenerationRequest();
        request.setMemberId(memberId);
        request.setDayType(dayType);
        return request;
    }
}
