package datn_gym.dto.response;

public record PaymentInfoResponse(
        String bankName,
        String bankAccountNumber,
        String bankAccountHolder,
        String transferPrefix,
        int pendingExpirationHours,
        boolean momoEnabled,
        String momoEnvironment) {
}
