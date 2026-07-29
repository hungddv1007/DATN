package datn_gym.service;

import datn_gym.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipExpirationService {

    private final MembershipRepository membershipRepository;

    /**
     * Đồng bộ trạng thái gói hết hạn mỗi giờ. Các truy vấn quyền quan trọng vẫn
     * kiểm tra endDate để không phụ thuộc hoàn toàn vào scheduler.
     */
    @Scheduled(cron = "${app.membership.expiration-cron:0 0 * * * *}")
    @Transactional
    public void expireOverdueMemberships() {
        int updated = membershipRepository.expireActiveMembershipsBefore(LocalDate.now());
        if (updated > 0) {
            log.info("Đã chuyển {} membership quá hạn sang EXPIRED", updated);
        }
    }
}
