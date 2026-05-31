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
            // Tat CSRF vi dung JWT (stateless)
            .csrf(csrf -> csrf.disable())

            // Cho phep CORS
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.addAllowedOrigin("http://localhost:5173"); // React dev server
                corsConfig.addAllowedOrigin("http://localhost:3000");
                corsConfig.addAllowedMethod("*");
                corsConfig.addAllowedHeader("*");
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))

            // Khong tao session (stateless JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Cau hinh quyen truy cap
            .authorizeHttpRequests(auth -> auth
                // API cong khai - khong can dang nhap
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/goi-tap/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/bai-viet/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tim-kiem/**").permitAll()

                // API Admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // API PT
                .requestMatchers("/api/pt/**").hasRole("PT")

                // API Hoi vien
                .requestMatchers("/api/hoi-vien/**").hasRole("MEMBER")

                // Tat ca API khac can dang nhap
                .anyRequest().authenticated()
            )

            // Them JWT filter truoc UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
