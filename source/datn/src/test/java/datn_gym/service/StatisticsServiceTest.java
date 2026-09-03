package datn_gym.service;

import datn_gym.dto.response.StatisticsResponse;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
    @Mock UserRepository userRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock MembershipRepository membershipRepository;
    @InjectMocks StatisticsService statisticsService;

    @Test
    void overviewCountsAllConfirmedTransactionsInCurrentMonth() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
        when(membershipRepository.countActiveMembershipsByPackage()).thenReturn(List.of());
        when(transactionRepository
                .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        "CONFIRMED", startOfMonth, startOfNextMonth))
                .thenReturn(3L);

        StatisticsResponse result = statisticsService.getOverview();

        assertEquals(3L, result.getTransactionsThisMonth());
        verify(transactionRepository)
                .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        "CONFIRMED", startOfMonth, startOfNextMonth);
    }
}
