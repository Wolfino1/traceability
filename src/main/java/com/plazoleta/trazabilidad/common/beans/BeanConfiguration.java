package com.plazoleta.trazabilidad.common.beans;

import com.plazoleta.trazabilidad.application.mappers.TraceabilityMapper;
import com.plazoleta.trazabilidad.domain.ports.in.TraceabilityServicePort;
import com.plazoleta.trazabilidad.domain.ports.out.OrderClientPort;
import com.plazoleta.trazabilidad.domain.ports.out.TraceabilityPersistencePort;
import com.plazoleta.trazabilidad.domain.ports.out.UserClientPort;
import com.plazoleta.trazabilidad.domain.usecase.TraceabilityUseCase;
import com.plazoleta.trazabilidad.infrastructure.adapters.persistence.TraceabilityPersistenceAdapter;
import com.plazoleta.trazabilidad.infrastructure.repositories.mongodb.TraceabilityRepository;
import com.plazoleta.trazabilidad.infrastructure.security.JwtAuthenticationFilter;
import com.plazoleta.trazabilidad.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class BeanConfiguration {

    private final TraceabilityRepository traceabilityRepository;
    private final OrderClientPort orderClientPort;
    private final UserClientPort userClientPort;
    private final TraceabilityMapper mapper;


    @Bean
    public TraceabilityServicePort traceabilityServicePort() {
        return new TraceabilityUseCase(traceabilityPersistencePort(),orderClientPort,userClientPort);
    }

    @Bean
    public TraceabilityPersistencePort traceabilityPersistencePort() {
        return new TraceabilityPersistenceAdapter(traceabilityRepository,mapper);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain permitTrazabilidad(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/api/v1/trazabilidad/**")
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

    @Bean
    @Order(2)
    public SecurityFilterChain protectedApiChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.GET, "/api/v1/trazabilidad/order/{id}").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/v1/trazabilidad/{orderId}/efficiency").hasRole("OWNER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}