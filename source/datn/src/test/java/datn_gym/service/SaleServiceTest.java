package datn_gym.service;

import datn_gym.dto.request.CreateSaleAccountRequest;
import datn_gym.dto.request.SaleCodeRequest;
import datn_gym.dto.response.SaleCodeResponse;
import datn_gym.entity.AiConversation;
import datn_gym.entity.AiMessage;
import datn_gym.entity.CommissionRecord;
import datn_gym.entity.SaleProfile;
import datn_gym.entity.SaleReferralCode;
import datn_gym.entity.Role;
import datn_gym.entity.User;
import datn_gym.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {
    @Mock SaleProfileRepository profileRepository;
    @Mock SaleReferralCodeRepository codeRepository;
    @Mock SalesCodeRedemptionRepository redemptionRepository;
    @Mock CommissionRecordRepository commissionRepository;
    @Mock AiConversationRepository conversationRepository;
    @Mock AiMessageRepository messageRepository;
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks SaleService saleService;

    @Test
    void createSaleAccount_rejectsExistingPhoneBeforeSaving() {
        CreateSaleAccountRequest request = new CreateSaleAccountRequest();
        request.setEmail("sale-new@gympro.com");
        request.setPassword("123456");
        request.setFullName("Nhân viên Sale mới");
        request.setPhone("0908123456");
        when(userRepository.existsByEmail("sale-new@gympro.com")).thenReturn(false);
        when(userRepository.existsByPhone("0908123456")).thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> saleService.createSaleAccount(request));

        assertEquals("Số điện thoại đã tồn tại", error.getMessage());
        verify(userRepository).existsByPhone("0908123456");
    }

    @Test
    void createSaleAccountCreatesExactlyThreeDefaultCodes() {
        CreateSaleAccountRequest request = new CreateSaleAccountRequest();
        request.setEmail("sale-new@gympro.com");
        request.setPassword("123456");
        request.setFullName("Nhân viên Sale mới");
        request.setPhone("0908111222");
        Role role = Role.builder().id(4).name("SALE").build();
        when(roleRepository.findByName("SALE")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(50);
            return user;
        });
        when(profileRepository.save(any(SaleProfile.class))).thenAnswer(invocation -> {
            SaleProfile profile = invocation.getArgument(0);
            profile.setId(8);
            return profile;
        });

        saleService.createSaleAccount(request);

        verify(codeRepository).saveAll(org.mockito.ArgumentMatchers.argThat(codes -> {
            List<SaleReferralCode> values = new java.util.ArrayList<>();
            codes.forEach(values::add);
            return values.size() == 3
                    && values.stream().map(SaleReferralCode::getCode).toList()
                    .equals(List.of("SALE50_1", "SALE50_2", "SALE50_3"));
        }));
    }

    @Test
    void markCommissionPaid_rejectsCommissionStillPending() {
        CommissionRecord commission = CommissionRecord.builder().status("PENDING").build();
        when(commissionRepository.findById(12L)).thenReturn(Optional.of(commission));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> saleService.markCommissionPaid(12L));

        assertEquals("Chỉ hoa hồng đã qua thời gian chờ mới có thể thanh toán", error.getMessage());
        verify(commissionRepository, never()).save(commission);
    }

    @Test
    void setOnlineFalseClosesEveryActiveConsultation() {
        User sale = User.builder().id(9).fullName("Nhân viên Sale").build();
        SaleProfile profile = SaleProfile.builder().id(3).user(sale).isOnline(true).build();
        AiConversation assigned = AiConversation.builder().id(21).handoffStatus("SALE_ASSIGNED").build();
        AiConversation joined = AiConversation.builder().id(22).handoffStatus("SALE_JOINED").build();
        when(profileRepository.findByUser_Email("sale@gympro.com")).thenReturn(Optional.of(profile));
        when(conversationRepository.findByAssignedSale_IdAndHandoffStatusInOrderByUpdatedAtDesc(
                9, List.of("SALE_ASSIGNED", "SALE_JOINED"))).thenReturn(List.of(assigned, joined));
        when(commissionRepository.findBySalesProfile_IdOrderByCreatedAtDesc(3)).thenReturn(List.of());

        var dashboard = saleService.setOnline("sale@gympro.com", false);

        assertEquals(false, dashboard.isOnline());
        assertEquals("CLOSED", assigned.getHandoffStatus());
        assertEquals("CLOSED", joined.getHandoffStatus());
        verify(conversationRepository).save(assigned);
        verify(conversationRepository).save(joined);
        verify(messageRepository, org.mockito.Mockito.times(2)).save(org.mockito.ArgumentMatchers.any(AiMessage.class));
    }

    @Test
    void updateCodeChangesEditableFields() {
        User sale = User.builder().id(9).build();
        SaleProfile profile = SaleProfile.builder().id(3).user(sale).build();
        SaleReferralCode existing = SaleReferralCode.builder().id(14).salesProfile(profile)
                .code("OLD_CODE").description("Cũ").discountPercent(10).oneTimePerMember(true).build();
        SaleCodeRequest request = new SaleCodeRequest();
        request.setCode("new_code");
        request.setDescription("Mã mới");
        request.setOneTimePerMember(false);
        when(profileRepository.findByUser_Email("sale@gympro.com")).thenReturn(Optional.of(profile));
        when(codeRepository.findById(14)).thenReturn(Optional.of(existing));
        when(codeRepository.findByCodeIgnoreCase("NEW_CODE")).thenReturn(Optional.empty());
        when(codeRepository.save(existing)).thenReturn(existing);

        SaleCodeResponse result = saleService.updateCode("sale@gympro.com", 14, request);

        assertEquals("NEW_CODE", result.getCode());
        assertEquals("Mã mới", result.getDescription());
        assertEquals(false, result.isOneTimePerMember());
    }

    @Test
    void setCodeActiveCanArchiveAndRestoreOwnedCode() {
        User sale = User.builder().id(9).build();
        SaleProfile profile = SaleProfile.builder().id(3).user(sale).build();
        SaleReferralCode existing = SaleReferralCode.builder().id(14).salesProfile(profile)
                .code("SALE_CODE").discountPercent(10).isActive(true).build();
        when(profileRepository.findByUser_Email("sale@gympro.com")).thenReturn(Optional.of(profile));
        when(codeRepository.findById(14)).thenReturn(Optional.of(existing));
        when(codeRepository.save(existing)).thenReturn(existing);

        SaleCodeResponse archived = saleService.setCodeActive("sale@gympro.com", 14, false);
        SaleCodeResponse restored = saleService.setCodeActive("sale@gympro.com", 14, true);

        assertEquals(false, archived.isActive());
        assertEquals(true, restored.isActive());
    }
}
