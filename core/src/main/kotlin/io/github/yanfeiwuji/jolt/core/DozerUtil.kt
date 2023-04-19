package io.github.yanfeiwuji.jolt.core

import com.github.dozermapper.core.DozerBeanMapperBuilder
import com.github.dozermapper.core.Mapper
import java.util.Objects

/**
 * @author  yanfeiwuji
 * @date  2023/4/16 13:54
 */
class DozerUtil {  

    companion object { 
        @JvmStatic
        private val MAPPER: Mapper = DozerBeanMapperBuilder.create()
            .withCustomFieldMapper { _, _, sourceFieldValue, _, _ ->
                Objects.isNull(sourceFieldValue)
            }.build()

        @JvmStatic
        private val SIMPLE_MAPPER: Mapper = DozerBeanMapperBuilder.create().build()

        @JvmStatic
        fun <T> merger(source: T, destination: T): T {
            MAPPER.map(source, destination)
            return destination
        }

        @JvmStatic
        fun <T> copy(source: T, clazz: Class<T>): T {
            return SIMPLE_MAPPER.map(source, clazz)
        }
    }
}
