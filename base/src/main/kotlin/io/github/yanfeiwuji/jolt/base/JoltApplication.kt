package io.github.yanfeiwuji.jolt.base

import io.github.yanfeiwuji.jolt.core.JoltApi
import io.github.yanfeiwuji.jolt.core.JoltApplication
import io.github.yanfeiwuji.jolt.core.JoltDao
import io.github.yanfeiwuji.jolt.core.JoltModel
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import jakarta.persistence.Entity
import org.springframework.boot.runApplication
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
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

@Entity
class SysUser : JoltModel() {
    var name: String? = null
    var age: Long? = null
}

interface SysUserDao : JoltDao<SysUser>

@RestController
@RequestMapping("sysUser")
@PreAuthorize("hasRole('ass')")
class SysUserApi(sysUserDao: SysUserDao) : JoltApi<SysUser>() {
    @GetMapping("/ass")
    fun ass(@AuthenticationPrincipal jwt: Jwt): String {
        println(jwt.subject)
        return "123";
    }
}
