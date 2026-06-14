package datn_gym.service;

import datn_gym.dto.response.StatisticsResponse;
import datn_gym.repository.MembershipRepository;
import datn_gym.repository.TransactionRepository;
import datn_gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MembershipRepository membershipRepository;

    public StatisticsResponse getOverview() {
        // 1. Tổng người dùng
        long totalUsers = userRepository.count();

        // 2. Doanh thu tháng này (Tính từ các giao dịch thành công)
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        java.math.BigDecimal revenue = transactionRepository.calculateRevenueSince(startOfMonth);
        long monthlyRevenue = revenue != null ? revenue.longValue() : 0;

        // 3. Số PT đang hoạt động
        long activePTs = userRepository.countByRole_NameAndStatus("PT", true);

        // 4. Số đăng ký tháng này
        long newRegistrations = membershipRepository.countByCreatedAtAfter(startOfMonth);

        return StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .monthlyRevenue(monthlyRevenue)
                .activePTs(activePTs)
                .newRegistrationsThisMonth(newRegistrations)
                .build();
    }
}
