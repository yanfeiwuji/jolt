package io.github.yanfeiwuji.jolt.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import java.util.Objects
import java.util.stream.Collectors
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


/**
 * @author  yanfeiwuji
 * @date  2023/4/16 21:23
 */
class JwtAuthConverter :
    Converter<Jwt, AbstractAuthenticationToken> {

    //val converter = JwtGrantedAuthoritiesConverter()
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        return JwtAuthenticationToken(jwt, extractResourceRoles(jwt), jwt.subject)
    }

    private fun extractResourceRoles(jwt: Jwt): Set<GrantedAuthority> {
        return ((jwt.claims["spring"] as Map<*, *>)["role"] as Set<*>)
            .filter(Objects::nonNull)
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
            .anyRequest().authenticated()
        http.oauth2ResourceServer().jwt()
            .jwtAuthenticationConverter(JwtAuthConverter())
        return http.build()
    }

}