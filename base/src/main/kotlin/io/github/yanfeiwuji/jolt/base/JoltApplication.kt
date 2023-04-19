package io.github.yanfeiwuji.jolt.base

import io.github.yanfeiwuji.jolt.core.JoltApi
import io.github.yanfeiwuji.jolt.core.JoltApplication
import io.github.yanfeiwuji.jolt.core.JoltDao
import io.github.yanfeiwuji.jolt.core.JoltModel
import jakarta.persistence.Entity
import org.springframework.boot.runApplication
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

@Entity
class SysApp : JoltModel() {
    var name: String? = null
    var code: Long? = null

    // description
    var description: String? = null

    // logo
    var logo: String? = null

    // url
    var url: String? = null

    // 是否启用
    var enabled: Boolean? = null

    // keycloak client id
    var clientId: String? = null

    // keycloak client secret
    var clientSecret: String? = null

    // type
    var type: String? = null

}

interface SysAppDao : JoltDao<SysApp>

@RestController
@RequestMapping("app")
class SysAppApi : JoltApi<SysApp>() {


}