package io.github.yanfeiwuji.jolt.core

import cn.hutool.json.JSONUtil
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import java.util.Objects


/**
 * @author  yanfeiwuji
 * @date  2023/4/16 21:23
 */
class JwtAuthConverter :
    Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        return JwtAuthenticationToken(jwt, extractResourceRoles(jwt), jwt.subject)
    }

    private fun extractResourceRoles(jwt: Jwt): Set<GrantedAuthority> {
        return (
                ((jwt.claims["resource_access"] as Map<*, *>)
                    ["spring"] as Map<*, *>)
                    ["roles"] as List<*>
                )
            .filter(Objects::nonNull)
            .distinct()
            .map { it.toString() }
            .map { SimpleGrantedAuthority("ROLE_$it") }
            .toSet()

    }
}

@Configuration
@EnableWebSecurity
class JoltSecurityConfig {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests()
            .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
            .permitAll()
            .anyRequest().authenticated()
        http.oauth2ResourceServer().jwt()
            .jwtAuthenticationConverter(JwtAuthConverter())
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }

}