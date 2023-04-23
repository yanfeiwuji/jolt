package io.github.yanfeiwuji.jolt.core

import cn.hutool.core.lang.Snowflake
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.web.config.EnableSpringDataWebSupport


/**
 * @author  yanfeiwuji
 * @date  2023/4/13 22:52
 */
class YamlPropertySourceFactory : PropertySourceFactory {
    override fun createPropertySource(
        name: String?,
        resource: EncodedResource
    ): org.springframework.core.env.PropertySource<*> {

        val a = YamlPropertySourceLoader()
            .load(resource.resource.filename, resource.resource)[0]
        println(a.source);
        return a
    }

}

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
@Import(
    JoltWebConfig::class,
    JoltSecurityConfig::class
)
@EnableAspectJAutoProxy
@EnableKeycloakSdk
annotation class JoltApplication

