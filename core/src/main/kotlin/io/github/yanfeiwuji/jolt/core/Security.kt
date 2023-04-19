package io.github.yanfeiwuji.jolt.core

import io.swagger.v3.oas.annotations.security.*
import org.springframework.beans.factory.annotation.Value
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
class JwtAuthConverter(private val clientId: String) :
    Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        return JwtAuthenticationToken(jwt, extractResourceRoles(jwt), jwt.subject)
    }

    private fun extractResourceRoles(jwt: Jwt): Set<GrantedAuthority> {
        return (((jwt.claims["resource_access"] as Map<*, *>)[clientId] as Map<*, *>)["roles"] as List<*>)
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

    @Value("\${keycloak.clientId}")
    val clientId: String = ""

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests()
            .requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
            .permitAll()
            .anyRequest().authenticated()

        http.oauth2ResourceServer().jwt()
            .jwtAuthenticationConverter(JwtAuthConverter(clientId))
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }


}