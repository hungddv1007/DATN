package datn_gym.payment.momo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MomoSignatureTest {

    @Test
    void createsDeterministicHmacSha256AndComparesSafely() {
        String signature = MomoSignature.hmacSha256(
                "The quick brown fox jumps over the lazy dog", "key");

        assertThat(signature).isEqualTo(
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8");
        assertThat(MomoSignature.matches(signature, signature.toUpperCase())).isTrue();
        assertThat(MomoSignature.matches(signature, "invalid")).isFalse();
    }
}
