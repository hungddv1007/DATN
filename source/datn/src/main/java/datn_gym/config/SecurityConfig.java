package datn_gym.config;

import datn_gym.security.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String[] allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                for (String origin : allowedOrigins) {
                    corsConfig.addAllowedOrigin(origin.trim());
                }
                corsConfig.addAllowedMethod("*");
                corsConfig.addAllowedHeader("*");
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // ✅ QUAN TRỌNG: Rule cụ thể phải đặt TRƯỚC rule chung
                // Spring Security đọc từ trên xuống, dừng ở rule đầu tiên match
                // Async dispatch của SSE đã được xác thực ở request ban đầu.
                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()

                // 1. Auth - công khai hoàn toàn
                .requestMatchers("/api/auth/**").permitAll()
                
                // File access
                .requestMatchers("/api/files/download/**").permitAll()
                .requestMatchers("/api/files/upload").authenticated()

                // 2. API Admin - chỉ ADMIN (đặt trước rule GET chung)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 3. API PT - chỉ PT (đặt trước rule GET chung)
                .requestMatchers("/api/pt/**").hasRole("PT")
                .requestMatchers("/api/nutrition/**").hasRole("PT")

                // Nhân viên kinh doanh
                .requestMatchers("/api/sale/**").hasRole("SALE")

                // 4. API Hội viên - chỉ MEMBER (đặt trước rule GET chung)
                .requestMatchers("/api/member/**").hasRole("MEMBER")

                // 5. API User profile - cần đăng nhập (bất kỳ role nào)
                .requestMatchers("/api/users/**").authenticated()

                // 5b. API Exercises - người dùng đã đăng nhập có thể xem thư viện bài tập
                .requestMatchers(HttpMethod.GET, "/api/exercises/**").authenticated()

                // 6. Các GET công khai (đặt SAU các rule role cụ thể)
                .requestMatchers(HttpMethod.GET, "/api/packages/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blogs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pt-profiles/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()

                // 7. Còn lại cần đăng nhập
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
