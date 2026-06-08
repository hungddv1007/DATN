package datn_gym.config;

import datn_gym.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                corsConfig.addAllowedOrigin("http://localhost:5173");
                corsConfig.addAllowedOrigin("http://localhost:3000");
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

                // 1. Auth - công khai hoàn toàn
                .requestMatchers("/api/auth/**").permitAll()

                // 2. API Admin - chỉ ADMIN (đặt trước rule GET chung)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 3. API PT - chỉ PT (đặt trước rule GET chung)
                .requestMatchers("/api/pt/**").hasRole("PT")

                // 4. API Hội viên - chỉ MEMBER (đặt trước rule GET chung)
                .requestMatchers("/api/hoi-vien/**").hasRole("MEMBER")

                // 5. Các GET công khai (đặt SAU các rule role cụ thể)
                .requestMatchers(HttpMethod.GET, "/api/goi-tap/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/bai-viet/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tim-kiem/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pt-profiles/**").permitAll()

                // 6. Còn lại cần đăng nhập
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}