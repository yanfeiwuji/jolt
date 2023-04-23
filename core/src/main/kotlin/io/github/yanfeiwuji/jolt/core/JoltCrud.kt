package io.github.yanfeiwuji.jolt.core

import cn.hutool.core.util.IdUtil
import io.github.perplexhub.rsql.RSQLJPASupport.toSpecification
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springdoc.core.annotations.ParameterObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.AuditorAware
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.util.*


/**
 * @author  yanfeiwuji
 * @date  2023/4/13 22:20
 */
class SnowflakeIdGenerator : IdentifierGenerator {
    override fun generate(p0: SharedSessionContractImplementor?, p1: Any?): Any {
        return IdUtil.getSnowflakeNextId()
    }

}

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class JoltModel {
    @Id
    @Schema(implementation = String::class)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "io.github.yanfeiwuji.jolt.core.SnowflakeIdGenerator")
    var id: Long? = null

    @CreatedBy
    var createBy: String? = null

    @CreatedDate
    @Schema(implementation = Long::class)
    var createdDate: Date? = null

    @LastModifiedBy
    var lastModifiedBy: String? = null

    @LastModifiedDate
    @Schema(implementation = Long::class)
    var lastModifiedDate: Date? = null

}

open class JoltLabelsModel : JoltModel() {
    @ElementCollection
    var labels: List<String>? = null
}

@NoRepositoryBean
interface JoltDao<T : JoltModel> : JpaRepository<T, Long>, JpaSpecificationExecutor<T>

open class JoltApi<T : JoltModel> {
    @Autowired
    private lateinit var joltDao: JoltDao<T>

    @GetMapping("{id}")
    @Operation(summary = "获取单个对象")
    operator fun get(
        @PathVariable("id")
        @Schema(implementation = String::class)
        id: Long
    ): T {
        return joltDao.findById(id).orElseThrow { JoltException(ResMsg.ENTITY_NOT_FOUND) }
    }

    @GetMapping("single")
    @Operation(summary = "查询单个对象")
    fun single(filter: String? = ""): T {
        return joltDao.findOne(toSpecification(filter))
            .orElseThrow { JoltException(ResMsg.ENTITY_NOT_FOUND) }
    }


    @GetMapping("page")
    @Operation(summary = "查询分页")
    fun page(filter: String? = "", @ParameterObject pageable: Pageable): Page<T> {
        return joltDao.findAll(toSpecification(filter), pageable)
    }


    /**
     * 最大返回 2000 个
     *
     * @param
     * @return
     */
    @GetMapping("list")
    @Operation(summary = "查询列表")
    fun list(filter: String? = ""): List<T> {
        return joltDao.findAll(toSpecification(filter), Pageable.ofSize(2000))
            .toList()
    }

    @PostMapping
    @Operation(summary = "添加对象")
    fun post(@RequestBody entity: T): T {
        return joltDao.save(entity)
    }

    @PutMapping("{id}")
    fun put(@PathVariable @Schema(implementation = String::class) id: Long, @RequestBody entity: T): T {

        return joltDao.findById(id).map {
            entity.id = it.id
            entity
        }.map(joltDao::save)
            .orElseThrow { JoltException(ResMsg.ENTITY_NOT_FOUND) }
    }

    @PatchMapping("{id}")
    @Operation(summary = "增量修改对象")
    fun patch(@PathVariable @Schema(implementation = String::class) id: Long, @RequestBody entity: T): T {

        return joltDao.findById(id).map {
            entity.id = it.id
            DozerUtil.merger(entity, it)
        }.map(joltDao::save)
            .orElseThrow { JoltException(ResMsg.ENTITY_NOT_FOUND) }
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") @Schema(implementation = String::class) id: Long): Boolean {
        joltDao.deleteById(id)
        return true
    }
}
