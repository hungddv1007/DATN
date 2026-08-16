package datn_gym.service;

import datn_gym.dto.request.CreateSaleAccountRequest;
import datn_gym.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {
    @Mock SaleProfileRepository profileRepository;
    @Mock SaleReferralCodeRepository codeRepository;
    @Mock SalesCodeRedemptionRepository redemptionRepository;
    @Mock CommissionRecordRepository commissionRepository;
    @Mock AiConversationRepository conversationRepository;
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
}
