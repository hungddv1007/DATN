package datn_gym.controller;

import datn_gym.dto.response.MessageResponse;
import datn_gym.security.JwtAuthenticationFilter;
import datn_gym.service.AuthService;
import datn_gym.service.ForgotPasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, ForgotPasswordController.class})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "app.google.client-id=test-client-id")
class AuthControllerContextTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private ForgotPasswordService forgotPasswordService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void authControllersLoadWithoutDuplicateMappings() throws Exception {
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("GymPro API is running!"));
    }

    @Test
    void verifyOtpUsesDedicatedControllerEndpoint() throws Exception {
        when(forgotPasswordService.verifyOtp(any()))
                .thenReturn(new MessageResponse("OTP hợp lệ"));

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"member@gympro.test","otp":"567128"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP hợp lệ"));
    }
}
