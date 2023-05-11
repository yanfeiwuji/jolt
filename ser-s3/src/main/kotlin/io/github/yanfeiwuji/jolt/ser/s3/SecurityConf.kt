package io.github.yanfeiwuji.jolt.ser.s3

import io.github.yanfeiwuji.jolt.core.HttpSecurityExt
import io.github.yanfeiwuji.jolt.core.JoltSecurityConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author  yanfeiwuji
 * @date  2023/5/3 18:06
 */
@Configuration
class SecurityConf {
    @Bean
    fun httpSecurityExt(): HttpSecurityExt =
        HttpSecurityExt {
            JoltSecurityConfig.defaultAuth(it)
            it.authorizeHttpRequests().requestMatchers("/s3/config/*")
                .permitAll()
        }
}