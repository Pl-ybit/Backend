package com.example.playbit.config;

import com.example.playbit.common.jwt.JwtProvider;
import com.example.playbit.security.JwtAuthenticationEntryPoint;
import com.example.playbit.security.JwtAuthenticationFilter;
import com.example.playbit.helper.TestSecurityController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CorsConfigTest.MinimalWebConfig.class)
@WebAppConfiguration
class CorsConfigTest {

    @Autowired
    WebApplicationContext webAppContext;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("허용된 localhost:3000 Origin의 Preflight 요청에 CORS 헤더가 반환된다")
    void 허용된_Origin_Preflight_CORS헤더_반환() throws Exception {
        mockMvc.perform(options("/api/v1/test/secured")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"));
    }

    @Test
    @DisplayName("허용되지 않은 Origin의 Preflight 요청은 CORS 헤더가 반환되지 않는다")
    void 허용되지_않은_Origin_CORS헤더_미반환() throws Exception {
        mockMvc.perform(options("/api/v1/test/secured")
                        .header(HttpHeaders.ORIGIN, "http://evil.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @EnableWebMvc
    @Import({SecurityConfig.class, CorsConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
    @Configuration
    static class MinimalWebConfig {

        @Bean
        JwtProvider jwtProvider() {
            String secret = Base64.getEncoder().encodeToString(
                    "test-secret-key-must-be-at-least-32-bytes!!".getBytes()
            );
            return new JwtProvider(secret, 3600000L);
        }

        @Bean
        tools.jackson.databind.ObjectMapper objectMapper() {
            return new tools.jackson.databind.ObjectMapper();
        }

        @Bean
        TestSecurityController testSecurityController() {
            return new TestSecurityController();
        }
    }
}
