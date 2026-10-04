package datn_gym.service;

import datn_gym.entity.Promotion;
import datn_gym.repository.GymPackageRepository;
import datn_gym.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock PromotionRepository promotionRepository;
    @Mock GymPackageRepository gymPackageRepository;

    private PromotionService service;

    @BeforeEach
    void setUp() {
        service = new PromotionService(promotionRepository, gymPackageRepository);
    }

    @Test
    void deletingPromotionReferencedByOldTransactionReturnsBusinessError() {
        Promotion promotion = Promotion.builder()
                .id(7)
                .code("WELCOME10")
                .currentUsage(0)
                .build();
        when(promotionRepository.findById(7)).thenReturn(Optional.of(promotion));
        doThrow(new DataIntegrityViolationException("FK_transactions_promotions"))
                .when(promotionRepository).flush();

        assertThatThrownBy(() -> service.deletePromotion(7))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).contains("đã từng được dùng");
                });
    }
}
