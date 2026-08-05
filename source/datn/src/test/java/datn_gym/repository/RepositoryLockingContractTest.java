package datn_gym.repository;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryLockingContractTest {

    @Test
    void membershipMutationLookupUsesPessimisticWriteLock() throws Exception {
        Lock lock = UserRepository.class
                .getMethod("findByEmailForMembershipUpdate", String.class)
                .getAnnotation(Lock.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void promotionReservationAndReleaseUsePessimisticWriteLocks() throws Exception {
        Lock reserveLock = PromotionRepository.class
                .getMethod("findValidPromotion", String.class, LocalDate.class, Integer.class)
                .getAnnotation(Lock.class);
        Lock releaseLock = PromotionRepository.class
                .getMethod("findByIdForUpdate", Integer.class)
                .getAnnotation(Lock.class);

        assertThat(reserveLock).isNotNull();
        assertThat(reserveLock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
        assertThat(releaseLock).isNotNull();
        assertThat(releaseLock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}
