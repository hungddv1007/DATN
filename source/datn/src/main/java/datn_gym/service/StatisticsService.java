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
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
        java.math.BigDecimal revenue = transactionRepository.calculateRevenueSince(startOfMonth);
        long monthlyRevenue = revenue != null ? revenue.longValue() : 0;

        // 3. Số PT đang hoạt động
        long activePTs = userRepository.countByRole_NameAndStatus("PT", true);

        // 4. Tính mọi giao dịch thành công trong tháng: đăng ký mới, gia hạn và nâng cấp.
        // Giao dịch PENDING hoặc CANCELLED không được đưa vào thống kê.
        long confirmedTransactions = transactionRepository
                .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        "CONFIRMED", startOfMonth, startOfNextMonth);

        // 5. Dữ liệu phân bổ gói tập
        java.util.List<Object[]> packageStats = membershipRepository.countActiveMembershipsByPackage();
        java.util.List<StatisticsResponse.ChartData> packageData = packageStats.stream()
                .map(row -> StatisticsResponse.ChartData.builder()
                        .name((String) row[0])
                        .value(((Number) row[1]).longValue())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        // 6. Dữ liệu doanh thu 6 tháng gần nhất
        java.util.List<StatisticsResponse.ChartData> revenueData = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate targetMonth = today.minusMonths(i);
            java.math.BigDecimal rev = transactionRepository.calculateMonthlyRevenue(targetMonth.getMonthValue(), targetMonth.getYear());
            revenueData.add(StatisticsResponse.ChartData.builder()
                    .name("Tháng " + targetMonth.getMonthValue())
                    .value(rev != null ? rev.longValue() : 0)
                    .build());
        }

        return StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .monthlyRevenue(monthlyRevenue)
                .activePTs(activePTs)
                .transactionsThisMonth(confirmedTransactions)
                .packageData(packageData)
                .revenueData(revenueData)
                .build();
    }
}
