package io.github.yanfeiwuji.jolt.core


import cn.hutool.extra.spring.SpringUtil
import io.swagger.v3.oas.annotations.security.*
import lombok.extern.slf4j.Slf4j
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.representations.idm.RoleRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.Order
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
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*


/**
 * @author  yanfeiwuji
 * @date  2023/4/16 21:23
 */
class JwtAuthConverter(private val clientId: String) : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        return JwtAuthenticationToken(jwt, extractResourceRoles(jwt), jwt.subject)
    }

    private fun extractResourceRoles(jwt: Jwt): Set<GrantedAuthority> {
        return Optional.ofNullable(jwt)
            .map(Jwt::getClaims)
            .map { it["resource_access"] }
            .map(Map::class.java::cast)
            .map { it[clientId] }
            .map(Map::class.java::cast)
            .map { it["roles"] }
            .map(List::class.java::cast)
            .map { roles ->
                roles.filter(Objects::nonNull)
                    .distinct()
                    .map { SimpleGrantedAuthority("ROLE_$it") }
                    .toSet()
            }.orElse(emptySet())
    }
}

@Configuration
@EnableWebSecurity
class JoltSecurityConfig(val realmResource: RealmResource) {


    @Value("\${keycloak.clientId}")
    val clientId: String = ""

    companion object {

        // roleResource create role
        @JvmStatic
        private fun create(rolesResource: RolesResource?, roleName: String) {
            // log

            rolesResource?.create(RoleRepresentation(roleName, roleName, false))
        }


        @JvmStatic
        fun defaultAuth(http: HttpSecurity) {
            // get clientId
            val rolesResource = SpringUtil.getBean(RolesResource::class.java)
            val currentRoles = rolesResource.list().map { it.name }

            // 生成权限
            val joltApis = SpringUtil.getBeansOfType(JoltApi::class.java)
            joltApis.map { it.value }.map { it.javaClass }.map {
                val baseUrl = it.getAnnotation(RequestMapping::class.java)?.value?.get(0) ?: "".trim('/')
                it.methods.filter { method ->
                    AnnotationUtils.getAnnotation(method, RequestMapping::class.java) != null
                }.forEach { method ->
                    val requestMapping =
                        AnnotationUtils.getAnnotation(method, RequestMapping::class.java) ?: return@forEach
                    requestMapping.value.size
                    val httpMethod = requestMapping.method[0].asHttpMethod()
                    val requestMethod = httpMethod.name().lowercase(Locale.getDefault())
                    val requestUrl =
                        if (requestMapping.value.isNotEmpty()) "/$baseUrl/${requestMapping.value[0].trim('/')}"
                        else {
                            baseUrl
                        }

                    val role = "${requestMethod.lowercase(Locale.getDefault())}:${method.name}:$requestUrl".replace(
                        "/", ":"
                    )

                    // add role
                    http.authorizeHttpRequests().requestMatchers(httpMethod, requestUrl).hasRole(role)
                    println("${httpMethod.name()} $requestUrl -> $role")
                    // add role to keycloak
                    if (!currentRoles.contains(role)) {
                        create(rolesResource, role)
                    }

                }
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun httpSecurityExt(): HttpSecurityExt {
        return HttpSecurityExt { defaultAuth(it) }
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity, httpSecurityExt: HttpSecurityExt): SecurityFilterChain {

        //  扩展
        httpSecurityExt.ext(http)
        // 开启swagger
        http.authorizeHttpRequests()
            .requestMatchers(
                "/",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/doc.html",
                "/webjars/**"
            )
            .permitAll()

        http.authorizeHttpRequests().anyRequest().authenticated()

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
            strs.filter(Objects::nonNull)
                .filter { it.isNotBlank() }
                .forEach {
                    defaultExtByName(http, it)
                }
        }

        @JvmStatic
        fun addRoleToClient(roleName: String) {
            // get clientId
            val rolesResource = SpringUtil.getBean(RolesResource::class.java)
            // add role to keycloak
            // get role
            val roles = rolesResource.list().map {
                it.name
            }
            if (roles.contains(roleName)) {
                return
            }
            rolesResource.create(RoleRepresentation(roleName, roleName, false))
        }
    }

    fun ext(http: HttpSecurity)
}

