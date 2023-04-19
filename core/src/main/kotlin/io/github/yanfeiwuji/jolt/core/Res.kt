package io.github.yanfeiwuji.jolt.core

import cn.hutool.core.date.DateUtil
import com.github.dozermapper.core.DozerBeanMapperBuilder
import com.github.dozermapper.core.Mapper
import cz.jirutka.rsql.parser.ParseException
import io.github.perplexhub.rsql.RSQLConfig
import io.github.perplexhub.rsql.RSQLSupport
import jakarta.persistence.EntityManager
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*


/**
 * @author  yanfeiwuji
 * @date  2023/4/13 22:04
 */

data class ResMsg(val errorCode: String, val errorMsg: String) {
    var ext: Any? = null;

    companion object {

        val SUCCESS: ResMsg = ResMsg("111111", "success")
        val SERVICE_ERROR: ResMsg = ResMsg("111500", "service 500")
        val INVALID_FORMAT: ResMsg = ResMsg("111400", "invalid_format")
        val ENTITY_NOT_FOUND: ResMsg = ResMsg("111404", "entity not found")
    }

    fun ext(ext: Any): ResMsg {
        val need = copy()
        need.ext = ext
        return need
    }
}

class JoltException(val resMsg: ResMsg) : RuntimeException()

@RestControllerAdvice
@EnableAutoConfiguration
class JoltWebConfig : WebMvcConfigurer {

    @EventListener(ApplicationStartedEvent::class)
    fun initRsqlSupport(event: ApplicationStartedEvent) {
        RSQLSupport(event.applicationContext.getBeansOfType(EntityManager::class.java))
        //
        RSQLSupport.addConverter(Date::class.java) { DateUtil.date(it.toLong() * 1000) }
    }

    @ExceptionHandler(JoltException::class)
    @ResponseStatus(HttpStatus.OK)
    fun handlerJoltException(joltException: JoltException): ResMsg {
        return joltException.resMsg
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParseException::class)
    fun handlerParseException(parseException: ParseException): ResMsg {
        return ResMsg.INVALID_FORMAT
    }

    /**
     * 这个需要开发人员处理
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handlerException(exception: Exception): ResMsg {
        exception.printStackTrace()
        return ResMsg.SERVICE_ERROR.ext(
            mapOf(
                "message" to exception.message
            )
        )
    }
}
