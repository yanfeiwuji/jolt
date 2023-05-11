package io.github.yanfeiwuji.jolt.base

import io.github.yanfeiwuji.jolt.core.HttpSecurityExt
import io.github.yanfeiwuji.jolt.core.JoltSecurityConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.script.ScriptEngineManager


/**
 * @author  yanfeiwuji
 * @date  2023/5/3 18:05
 */
@Configuration
class SecurityConf {
    @Bean
    fun httpSecurityExt(): HttpSecurityExt =
        HttpSecurityExt {
            JoltSecurityConfig.defaultAuth(it)
            val keycloakAppRole = "keycloak-app"
            it.authorizeHttpRequests().requestMatchers("/keycloak/app")
                .hasAnyRole(keycloakAppRole)
            HttpSecurityExt.addRoleToClient(keycloakAppRole)
        }
}
