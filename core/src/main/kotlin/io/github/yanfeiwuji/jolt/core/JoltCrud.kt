package io.github.yanfeiwuji.jolt.core

import cn.hutool.core.util.IdUtil
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.GenericGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
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
open class JoltModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "io.github.yanfeiwuji.jolt.core.SnowflakeIdGenerator")
    private val id: Long? = null

    @CreatedBy
    private val createBy: String? = null

    @CreatedDate
    private val createdDate: Date? = null

    @LastModifiedBy
    private val lastModifiedBy: String? = null

    @LastModifiedDate
    private val LastModifiedDate: Date? = null
}

@NoRepositoryBean
interface JoltDao<T : JoltModel> : JpaRepository<T, Long>, QuerydslPredicateExecutor<T>