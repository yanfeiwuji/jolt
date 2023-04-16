package io.github.yanfeiwuji.jolt.core

import cn.hutool.core.lang.Snowflake
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.web.config.EnableSpringDataWebSupport

/**
 * @author  yanfeiwuji
 * @date  2023/4/13 22:52
 */
@SpringBootApplication
@EnableSpringDataWebSupport
@EnableJpaRepositories
@RegisterReflectionForBinding(
    SnowflakeIdGenerator::class,
    Snowflake::class,
    PathItem.HttpMethod::class,
    Parameter.StyleEnum::class,
    Page::class,
    PageImpl::class
)
@EnableJpaAuditing
@Import(JoltCrudConfiguration::class, JoltSecurityConfig::class)
@EnableAspectJAutoProxy
annotation class JoltApplication

