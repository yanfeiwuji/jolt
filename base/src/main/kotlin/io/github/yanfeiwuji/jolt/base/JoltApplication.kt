package io.github.yanfeiwuji.jolt.base

import io.github.yanfeiwuji.jolt.core.JoltApplication
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.ClientRepresentation
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author  yanfeiwuji
 * @date  2023/4/13 22:52
 */
@JoltApplication
class JoltBaseApplication

fun main(args: Array<String>) {
    runApplication<JoltBaseApplication>(*args)
}


@RestController
@RequestMapping("keycloak/app")
class KeycloakAppController(val realmResource: RealmResource) {
    @GetMapping
    fun list(): List<ClientRepresentation> {
        return realmResource
            .clients()
            // 获取所有的
            .findAll(true)
            // 过滤掉不显示在控制台的
            .filter {
                it.isAlwaysDisplayInConsole
            }
    }
}
