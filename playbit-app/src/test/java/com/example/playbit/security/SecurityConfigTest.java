package com.example.playbit.security;

import com.example.playbit.common.jwt.JwtProvider;
import com.example.playbit.config.CorsConfig;
import com.example.playbit.config.SecurityConfig;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfigTest.MinimalWebConfig.class)
@WebAppConfiguration
class SecurityConfigTest {

    @Autowired WebApplicationContext webAppContext;
    @Autowired JwtProvider jwtProvider;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("유효한 JWT 없이 인증 필요 API 호출 시 401 반환")
    void JWT없이_보호된_엔드포인트_401() throws Exception {
        mockMvc.perform(get("/api/v1/test/secured"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("C003"));
    }

    @Test
    @DisplayName("유효한 JWT로 요청 시 정상 처리된다")
    void 유효한_JWT로_보호된_엔드포인트_200() throws Exception {
        String token = jwtProvider.generate(1L, "test@example.com", "USER", false);

        mockMvc.perform(get("/api/v1/test/secured")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JWT 없이 permitAll 경로 요청 시 Security가 차단하지 않는다")
    void JWT없이_permitAll_경로_접근가능() throws Exception {
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
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
