package io.github.yanfeiwuji.jolt.base

import io.github.yanfeiwuji.jolt.core.JoltApi
import io.github.yanfeiwuji.jolt.core.JoltApplication
import io.github.yanfeiwuji.jolt.core.JoltDao
import io.github.yanfeiwuji.jolt.core.JoltModel
import jakarta.persistence.Entity
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.EnableWebMvc

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
class SysUserApi : JoltApi<SysUser>() {
    @GetMapping("/ass")
    fun ass() = "1dsdsds---23"
}
