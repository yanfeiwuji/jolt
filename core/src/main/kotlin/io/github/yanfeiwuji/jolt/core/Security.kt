package io.github.yanfeiwuji.jolt.core

import cn.hutool.core.util.StrUtil
import io.swagger.v3.oas.annotations.security.*
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import lombok.extern.slf4j.Slf4j
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RoleResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.representations.idm.RoleRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import java.util.Objects
import kotlin.reflect.full.hasAnnotation


/**
 * @author  yanfeiwuji
 * @date  2023/4/16 21:23
 */
class JwtAuthConverter(private val clientId: String) : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        return JwtAuthenticationToken(jwt, extractResourceRoles(jwt), jwt.subject)
    }

    private fun extractResourceRoles(jwt: Jwt): Set<GrantedAuthority> {
        return (((jwt.claims["resource_access"] as Map<*, *>)[clientId] as Map<*, *>)["roles"] as List<*>).filter(
            Objects::nonNull
        ).distinct()
            .map { it.toString() }
            .map { SimpleGrantedAuthority("ROLE_$it") }
            .toSet()

    }
}

@Configuration
@EnableWebSecurity
class JoltSecurityConfig(val realmResource: RealmResource) {



    @Value("\${keycloak.clientId}")
    val clientId: String = ""

    @Bean
    @ConditionalOnMissingBean
    fun httpSecurityExt(entityManager: EntityManager): HttpSecurityExt {
        return HttpSecurityExt { http ->
            // 生成角色
            val rolesResource =

                realmResource.clients().findByClientId(clientId)[0].id?.let { realmResource.clients().get(it).roles() }
            // 生成权限

            entityManager.metamodel.entities.map {
                it.name
            }.map(StrUtil::lowerFirst).forEach {

                // 生成权限
                HttpSecurityExt.defaultExtByName(http, it)

                // 删除角色
                rolesResource
                    ?.list()
                    ?.map(RoleRepresentation::getName)
                    ?.forEach(rolesResource::deleteRole)
                // 生成角色
                create(rolesResource, "get:$it")
                create(rolesResource, "get:list:$it")
                create(rolesResource, "get:page:$it")
                create(rolesResource, "post:$it")
                create(rolesResource, "put:$it")
                create(rolesResource, "patch:$it")
                create(rolesResource, "delete:$it")


            }

        }
    }

    // roleResource create role
    private fun create(rolesResource: RolesResource?, roleName: String) {
        rolesResource?.create(RoleRepresentation(roleName, roleName, false))
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, httpSecurityExt: HttpSecurityExt): SecurityFilterChain {

        //  扩展
        httpSecurityExt.ext(http)
        // 开启swagger
        http.authorizeHttpRequests().requestMatchers("/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
            .permitAll().anyRequest().authenticated()

        // 开启跨域
        http.cors()
        // 开启csrf
        http.csrf().disable()
        // 开启oauth2
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(JwtAuthConverter(clientId))
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return http.build()
    }


}

@FunctionalInterface
fun interface HttpSecurityExt {
    companion object {
        @JvmStatic
        fun defaultExtByName(http: HttpSecurity, string: String) {
            http.authorizeHttpRequests().requestMatchers(HttpMethod.GET, "$string/{id}").hasRole("GET:$string")
                .requestMatchers(HttpMethod.GET, "$string/single").hasRole("GET:list:$string")
                .requestMatchers(HttpMethod.GET, "$string/list").hasRole("GET:list:$string")
                .requestMatchers(HttpMethod.GET, "$string/page").hasRole("GET:page:$string")
                .requestMatchers(HttpMethod.POST, string).hasRole("POST:$string")
                .requestMatchers(HttpMethod.PUT, "$string/**").hasRole("PUT:$string")
                .requestMatchers(HttpMethod.PATCH, "$string/**").hasRole("PATCH:$string")
                .requestMatchers(HttpMethod.DELETE, "$string/**").hasRole("DELETE:$string")
        }

        @JvmStatic
        fun defaultExtByNames(http: HttpSecurity, strs: List<String>) {
            strs.filter(Objects::nonNull).filter { it.isNotBlank() }.forEach {
                defaultExtByName(http, it)
            }
        }
    }

    fun ext(http: HttpSecurity)
}

