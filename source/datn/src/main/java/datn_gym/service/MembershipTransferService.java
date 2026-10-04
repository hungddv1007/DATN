package datn_gym.service;

import datn_gym.dto.request.MembershipTransferAcceptRequest;
import datn_gym.dto.request.MembershipTransferCreateRequest;
import datn_gym.dto.request.TransferVerificationRequest;
import datn_gym.dto.response.MembershipTransferResponse;
import datn_gym.entity.*;
import datn_gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipTransferService {
    private static final String PENDING = "PENDING_RECIPIENT";
    private final MembershipTransferRepository transferRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PackageHoldPolicyRepository holdPolicyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final OtpService otpService;
    private final PolicyService policyService;
    private final NotificationService notificationService;

    public void verifySenderAndSendOtp(String email, TransferVerificationRequest request) {
        User sender = requireMember(email);
        if (sender.getEmail().equalsIgnoreCase(request.getRecipientEmail())) {
            throw new IllegalArgumentException("Không thể chuyển gói cho chính mình");
        }
        requireRecipient(request.getRecipientEmail());
        boolean verified = request.getPassword() != null && !request.getPassword().isBlank()
                && passwordEncoder.matches(request.getPassword(), sender.getPassword());
        if (!verified) verified = authService.verifyGoogleIdentity(request.getGoogleIdToken(), sender.getEmail());
        if (!verified) throw new IllegalArgumentException("Mật khẩu hoặc xác thực Google không chính xác");
        otpService.generateAndSendOtp(sender.getEmail());
    }

    @Transactional
    public MembershipTransferResponse create(String email, MembershipTransferCreateRequest request,
                                             String ip, String userAgent) {
        User sender = requireMember(email);
        if (!otpService.validateOtp(sender.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không chính xác hoặc đã hết hạn");
        }
        User recipient = requireRecipient(request.getRecipientEmail());
        if (sender.getId().equals(recipient.getId())) throw new IllegalArgumentException("Không thể chuyển gói cho chính mình");

        Membership source = membershipRepository.findCurrentByUserIdForUpdate(
                        sender.getId(), List.of("ACTIVE"))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chỉ gói đang hoạt động mới có thể chuyển nhượng; hãy kết thúc bảo lưu trước nếu có"));
        if (transactionRepository.existsByMembership_IdAndStatus(source.getId(), "PENDING")) {
            throw new IllegalArgumentException("Gói đang có giao dịch chờ xử lý");
        }
        if (transferRepository.existsBySourceMembership_IdAndStatus(source.getId(), PENDING)) {
            throw new IllegalArgumentException("Gói đã có một yêu cầu chuyển nhượng đang chờ");
        }
        if (transferRepository.countBySender_IdAndStatus(sender.getId(), "ACCEPTED") >= 3) {
            throw new IllegalArgumentException("Tài khoản đã sử dụng hết 3 lần chuyển nhượng");
        }

        int remaining = remainingDays(source);
        if (remaining <= 3) throw new IllegalArgumentException("Gói phải còn trên 3 ngày để chuyển nhượng");
        PolicyVersion policy = policyService.requireAcceptedVersion(
                request.getTransferPolicyVersionId(), "TRANSFER_POLICY");

        MembershipTransfer transfer = transferRepository.save(MembershipTransfer.builder()
                .sourceMembership(source).sender(sender).recipient(recipient)
                .status(PENDING).remainingDaysAtRequest(remaining)
                .expiresAt(LocalDateTime.now().plusDays(60)).build());
        policyService.recordAcceptance(sender, policy, null, "TRANSFER_SEND", ip, userAgent);
        notificationService.sendSystemNotification(recipient.getId(), "Bạn nhận được một gói tập",
                sender.getFullName() + " đã gửi yêu cầu chuyển nhượng gói "
                        + source.getGymPackage().getName() + ". Yêu cầu có hiệu lực trong 60 ngày.");
        return toResponse(transfer);
    }

    public void sendRecipientOtp(String email, Long transferId) {
        User recipient = requireMember(email);
        MembershipTransfer transfer = requireIncoming(transferId, recipient);
        requirePendingAndNotExpired(transfer);
        otpService.generateAndSendOtp(recipient.getEmail());
    }

    @Transactional
    public MembershipTransferResponse accept(String email, Long transferId,
                                             MembershipTransferAcceptRequest request,
                                             String ip, String userAgent) {
        User recipient = requireMember(email);
        MembershipTransfer transfer = transferRepository.findByIdForUpdate(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu chuyển nhượng"));
        if (!transfer.getRecipient().getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("Bạn không phải người nhận của yêu cầu này");
        }
        requirePendingAndNotExpired(transfer);
        if (!otpService.validateOtp(recipient.getEmail(), request.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không chính xác hoặc đã hết hạn");
        }
        PolicyVersion policy = policyService.requireAcceptedVersion(
                request.getTransferPolicyVersionId(), "TRANSFER_POLICY");

        Membership source = membershipRepository.findByIdForUpdate(transfer.getSourceMembership().getId())
                .orElseThrow(() -> new IllegalArgumentException("Gói nguồn không còn tồn tại"));
        if (!"ACTIVE".equals(source.getStatus()) || source.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Gói nguồn không còn hoạt động");
        }
        int remaining = remainingDays(source);
        int deduction = calculateDeductionDays(remaining);
        int transferredDays = remaining - deduction;
        if (transferredDays <= 0) throw new IllegalArgumentException("Gói không còn đủ ngày để chuyển");

        Membership current = membershipRepository.findCurrentByUserIdForUpdate(
                recipient.getId(), List.of("ACTIVE", "PAUSED", "PENDING")).orElse(null);
        if (current != null && "PENDING".equals(current.getStatus())) {
            throw new IllegalArgumentException("Bạn đang có giao dịch mua gói chờ duyệt");
        }
        if (current != null && transferRepository.existsBySourceMembership_IdAndStatus(current.getId(), PENDING)) {
            throw new IllegalArgumentException("Gói hiện tại của bạn đang có yêu cầu chuyển nhượng chờ xử lý");
        }
        boolean samePackage = current != null
                && current.getGymPackage().getId().equals(source.getGymPackage().getId());
        if (current != null && !samePackage && !request.isConfirmedReplacement()) {
            throw new IllegalArgumentException(
                    "Gói nhận khác loại và sẽ ghi đè toàn bộ gói hiện tại. Hãy xác nhận rõ việc thay thế");
        }

        if (samePackage) {
            current.setEndDate(current.getEndDate().plusDays(transferredDays));
            current.setDurationDays(current.getDurationDays() + transferredDays);
            refreshHoldPolicy(current);
            membershipRepository.save(current);
        } else {
            if (current != null) {
                current.setStatus("REPLACED_BY_TRANSFER");
                current.setPausedAt(null);
                current.setHoldUntil(null);
                membershipRepository.save(current);
            }
            Membership received = Membership.builder().user(recipient).gymPackage(source.getGymPackage())
                    .pt(null).startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(transferredDays))
                    .status("ACTIVE").durationDays(transferredDays).dailyPrice(source.getDailyPrice()).build();
            refreshHoldPolicy(received);
            membershipRepository.save(received);
        }

        source.setStatus("TRANSFERRED");
        source.setPausedAt(null);
        source.setHoldUntil(null);
        membershipRepository.save(source);
        transfer.setRemainingDaysAtAccept(remaining);
        transfer.setDeductedDays(deduction);
        transfer.setTransferredDays(transferredDays);
        transfer.setStatus("ACCEPTED");
        transfer.setAcceptedAt(LocalDateTime.now());
        transferRepository.save(transfer);
        policyService.recordAcceptance(recipient, policy, null, "TRANSFER_RECEIVE", ip, userAgent);
        notificationService.sendSystemNotification(transfer.getSender().getId(), "Chuyển nhượng thành công",
                recipient.getFullName() + " đã nhận gói tập. " + transferredDays + " ngày đã được chuyển.");
        return toResponse(transfer);
    }

    @Transactional
    public MembershipTransferResponse reject(String email, Long transferId) {
        User recipient = requireMember(email);
        MembershipTransfer transfer = transferRepository.findByIdForUpdate(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu chuyển nhượng"));
        if (!transfer.getRecipient().getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("Bạn không phải người nhận của yêu cầu này");
        }
        requirePendingAndNotExpired(transfer);
        transfer.setStatus("REJECTED");
        transfer.setRejectedAt(LocalDateTime.now());
        transferRepository.save(transfer);
        notificationService.sendSystemNotification(transfer.getSender().getId(), "Yêu cầu chuyển nhượng bị từ chối",
                recipient.getFullName() + " đã từ chối nhận gói tập.");
        return toResponse(transfer);
    }

    @Transactional
    public MembershipTransferResponse cancel(String email, Long transferId) {
        User sender = requireMember(email);
        MembershipTransfer transfer = transferRepository.findByIdForUpdate(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu chuyển nhượng"));
        if (!transfer.getSender().getId().equals(sender.getId())) throw new IllegalArgumentException("Không có quyền hủy");
        requirePendingAndNotExpired(transfer);
        transfer.setStatus("CANCELLED");
        return toResponse(transferRepository.save(transfer));
    }

    public List<MembershipTransferResponse> outgoing(String email) {
        return transferRepository.findBySender_IdOrderByCreatedAtDesc(requireMember(email).getId())
                .stream().map(this::toResponse).toList();
    }

    public List<MembershipTransferResponse> incoming(String email) {
        return transferRepository.findByRecipient_IdOrderByCreatedAtDesc(requireMember(email).getId())
                .stream().map(this::toResponse).toList();
    }

    @Scheduled(cron = "${app.membership.transfer-expiration-cron:0 15 * * * *}")
    @Transactional
    public void expireTransfers() {
        for (MembershipTransfer transfer : transferRepository
                .findByStatusAndExpiresAtBefore(PENDING, LocalDateTime.now())) {
            transfer.setStatus("EXPIRED");
            transferRepository.save(transfer);
        }
    }

    private User requireMember(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        if (!"MEMBER".equals(user.getRole().getName())) throw new IllegalArgumentException("Chức năng chỉ dành cho member");
        return user;
    }

    private User requireRecipient(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản Member với email này"));
        if (!"MEMBER".equals(user.getRole().getName()) || Boolean.FALSE.equals(user.getStatus())) {
            throw new IllegalArgumentException(
                    "Email người nhận phải thuộc tài khoản Member đang hoạt động; không thể sử dụng tài khoản PT, Sale hoặc Admin");
        }
        return user;
    }

    private MembershipTransfer requireIncoming(Long id, User recipient) {
        MembershipTransfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu chuyển nhượng"));
        if (!transfer.getRecipient().getId().equals(recipient.getId())) throw new IllegalArgumentException("Không có quyền truy cập");
        return transfer;
    }

    private void requirePendingAndNotExpired(MembershipTransfer transfer) {
        if (!PENDING.equals(transfer.getStatus())) throw new IllegalArgumentException("Yêu cầu không còn chờ xử lý");
        if (transfer.getExpiresAt().isBefore(LocalDateTime.now())) {
            transfer.setStatus("EXPIRED");
            transferRepository.save(transfer);
            throw new IllegalArgumentException("Yêu cầu chuyển nhượng đã hết hạn");
        }
    }

    private int remainingDays(Membership membership) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate()));
    }

    static int calculateDeductionDays(int remaining) {
        return Math.min(30, Math.max(3, (int) Math.ceil(remaining * 0.10)));
    }

    private void refreshHoldPolicy(Membership membership) {
        PackageHoldPolicy p = holdPolicyRepository.findApplicable(
                membership.getGymPackage().getId(), membership.getDurationDays()).orElse(null);
        membership.setHoldMaxTimes(p == null ? 0 : p.getMaxHoldTimes());
        membership.setHoldMaxDaysPerTime(p == null ? 0 : p.getMaxDaysPerHold());
        membership.setHoldMaxTotalDays(p == null ? 0 : p.getMaxTotalHoldDays());
    }

    private MembershipTransferResponse toResponse(MembershipTransfer transfer) {
        Membership current = membershipRepository.findByUser_IdAndStatusIn(
                transfer.getRecipient().getId(), List.of("ACTIVE", "PAUSED")).orElse(null);
        int remaining = "ACCEPTED".equals(transfer.getStatus()) && transfer.getRemainingDaysAtAccept() != null
                ? transfer.getRemainingDaysAtAccept()
                : Math.min(transfer.getRemainingDaysAtRequest(), remainingDays(transfer.getSourceMembership()));
        boolean replaces = current != null && !current.getGymPackage().getId()
                .equals(transfer.getSourceMembership().getGymPackage().getId());
        return MembershipTransferResponse.builder().id(transfer.getId())
                .sourceMembershipId(transfer.getSourceMembership().getId())
                .senderId(transfer.getSender().getId()).senderName(transfer.getSender().getFullName())
                .recipientId(transfer.getRecipient().getId()).recipientName(transfer.getRecipient().getFullName())
                .recipientEmail(transfer.getRecipient().getEmail())
                .packageName(transfer.getSourceMembership().getGymPackage().getName())
                .remainingDays(remaining).estimatedDeductionDays(calculateDeductionDays(Math.max(remaining, 1)))
                .transferredDays(transfer.getTransferredDays()).status(transfer.getStatus())
                .replacesCurrentPackage(replaces)
                .recipientCurrentPackage(current == null ? null : current.getGymPackage().getName())
                .recipientCurrentRemainingDays(current == null ? null : remainingDays(current))
                .expiresAt(transfer.getExpiresAt()).createdAt(transfer.getCreatedAt())
                .acceptedAt(transfer.getAcceptedAt()).build();
    }
}
