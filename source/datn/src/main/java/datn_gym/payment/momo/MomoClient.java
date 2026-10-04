package datn_gym.payment.momo;

import datn_gym.config.MomoProperties;
import datn_gym.dto.request.MomoIpnRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class MomoClient {

    private static final String CREATE_PATH = "/v2/gateway/api/create";
    private static final String QUERY_PATH = "/v2/gateway/api/query";

    private final MomoProperties properties;
    private final WebClient webClient;

    public MomoClient(MomoProperties properties, WebClient.Builder builder) {
        this.properties = properties;
        this.webClient = builder.baseUrl(properties.effectiveBaseUrl()).build();
    }

    public MomoGatewayResponse createPayment(
            String orderId,
            String requestId,
            long amount,
            String orderInfo,
            String redirectUrl) {
        properties.requireConfigured();
        String extraData = "";
        String requestType = "captureWallet";
        String ipnUrl = properties.ipnUrl().trim();
        String partnerCode = properties.partnerCode().trim();

        String rawSignature = "accessKey=" + properties.accessKey().trim()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("partnerName", properties.effectiveStoreName());
        body.put("storeId", "GymPro");
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", redirectUrl);
        body.put("ipnUrl", ipnUrl);
        body.put("requestType", requestType);
        body.put("extraData", extraData);
        body.put("autoCapture", true);
        body.put("lang", "vi");
        body.put("signature", MomoSignature.hmacSha256(
                rawSignature, properties.secretKey().trim()));

        return post(CREATE_PATH, body);
    }

    public MomoGatewayResponse queryPayment(String orderId, String requestId) {
        properties.requireConfigured();
        String partnerCode = properties.partnerCode().trim();
        String rawSignature = "accessKey=" + properties.accessKey().trim()
                + "&orderId=" + orderId
                + "&partnerCode=" + partnerCode
                + "&requestId=" + requestId;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("requestId", requestId);
        body.put("orderId", orderId);
        body.put("lang", "vi");
        body.put("signature", MomoSignature.hmacSha256(
                rawSignature, properties.secretKey().trim()));
        return post(QUERY_PATH, body);
    }

    public boolean isValidIpnSignature(MomoIpnRequest request) {
        properties.requireConfigured();
        String rawSignature = "accessKey=" + properties.accessKey().trim()
                + "&amount=" + value(request.getAmount())
                + "&extraData=" + value(request.getExtraData())
                + "&message=" + value(request.getMessage())
                + "&orderId=" + value(request.getOrderId())
                + "&orderInfo=" + value(request.getOrderInfo())
                + "&orderType=" + value(request.getOrderType())
                + "&partnerCode=" + value(request.getPartnerCode())
                + "&payType=" + value(request.getPayType())
                + "&requestId=" + value(request.getRequestId())
                + "&responseTime=" + value(request.getResponseTime())
                + "&resultCode=" + value(request.getResultCode())
                + "&transId=" + value(request.getTransId());
        String expected = MomoSignature.hmacSha256(
                rawSignature, properties.secretKey().trim());
        return MomoSignature.matches(expected, request.getSignature());
    }

    private MomoGatewayResponse post(String path, Map<String, Object> body) {
        MomoGatewayResponse response = webClient.post()
                .uri(path)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(MomoGatewayResponse.class)
                .block(Duration.ofSeconds(35));
        if (response == null) {
            throw new IllegalStateException("MoMo không trả về dữ liệu.");
        }
        return response;
    }

    private String value(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
