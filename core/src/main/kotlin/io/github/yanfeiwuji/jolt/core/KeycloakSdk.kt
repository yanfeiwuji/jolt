package io.github.yanfeiwuji.jolt.core

import cn.hutool.extra.spring.SpringUtil
import org.eclipse.microprofile.config.inject.ConfigProperties
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.representations.idm.ClientRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.lang.annotation.*

/**
 * @author  yanfeiwuji
 * @date  2023/4/19 22:06
 */
@ConfigurationProperties(prefix = "keycloak-admin")
@Component
class KeycloakProperty {
    var serverUrl: String? = null
    var realm: String? = null
    var username: String? = null
    var password: String? = null
}

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(KeycloakSdk::class)
annotation class EnableKeycloakSdk

@Import(KeycloakProperty::class)
class KeycloakSdk {

    @Value("\${keycloak.clientId}")
    val clientId: String = ""

    @Bean
    fun rolesResource(
        realmResource: RealmResource
    ): RolesResource {
        val clients = realmResource.clients().findByClientId(clientId)
        val clientRepresentation: ClientRepresentation?
        if (clients.isEmpty()) {
            val client = ClientRepresentation()
            client.clientId = clientId
            client.isBearerOnly = true
            client.isAlwaysDisplayInConsole = true
            realmResource.clients().create(client)
            clientRepresentation = realmResource.clients().findByClientId(clientId)[0]
        } else {
            clientRepresentation = clients[0]
        }
        clientRepresentation!!
        return realmResource.clients().get(clientRepresentation.id).roles()
    }

    @Bean
    fun realmResource(
        keycloakProperty: KeycloakProperty,
        oAuth2ResourceServerProperties: OAuth2ResourceServerProperties
    ): RealmResource {
        // issuerUri 获取 realm 获取 用 / 截取的最后一个
        val jwt = oAuth2ResourceServerProperties.jwt
        return Keycloak
            .getInstance(
                keycloakProperty.serverUrl,
                keycloakProperty.realm,
                keycloakProperty.username,
                keycloakProperty.password,
                "admin-cli"
            )
            .realm(jwt.issuerUri?.split("/")?.last())
    }


}