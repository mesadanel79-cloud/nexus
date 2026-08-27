package application.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration: authentication and authorization differentiated
 * by role (Comprador, Vendedor, Operador Logistico, Administrador,
 * Supervisor), as defined in the architecture document.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public catalog: readable by anyone.
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        // Seller onboarding and catalog publishing: administrators/sellers.
                        .requestMatchers(HttpMethod.POST, "/api/v1/sellers")
                        .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products")
                        .hasAnyRole("VENDEDOR", "ADMINISTRADOR")
                        // Cart and orders: buyers.
                        .requestMatchers("/api/v1/cart/**", "/api/v1/orders/**")
                        .hasAnyRole("COMPRADOR", "ADMINISTRADOR", "SUPERVISOR")
                        // Shipments: logistics operators.
                        .requestMatchers("/api/v1/shipments/**")
                        .hasAnyRole("OPERADOR_LOGISTICO", "ADMINISTRADOR")
                        // Returns/refunds: buyers request; staff evaluates.
                        .requestMatchers("/api/v1/returns/**")
                        .hasAnyRole("COMPRADOR", "ADMINISTRADOR", "SUPERVISOR",
                                "OPERADOR_LOGISTICO")
                        // Everything else requires authentication.
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.realmName("NexusMarket"))
                .formLogin(form -> form.disable());
        return http.build();
    }
}