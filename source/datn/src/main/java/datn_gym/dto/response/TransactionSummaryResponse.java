package datn_gym.dto.response;

public record TransactionSummaryResponse(
        long totalTransactions,
        long cancelledTransactions,
        long pendingTransactions) {
}
