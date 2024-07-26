package study.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import study.filter.JwtRequestFilter;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilter() {
        FilterRegistrationBean<JwtRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtRequestFilter);
        registrationBean.addUrlPatterns("/api/*"); // 필터가 적용될 URL 패턴
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 1); // 필터의 우선순위 설정
        return registrationBean;
    }
}
