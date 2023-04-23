package io.github.yanfeiwuji.jolt.ser.s3

import io.github.yanfeiwuji.jolt.core.JoltApplication
import org.springframework.boot.runApplication

/**
 * @author  yanfeiwuji
 * @date  2023/4/23 23:18
 */
@JoltApplication
class JoltSerS3Application

// start
fun main(args: Array<String>) {
    runApplication<JoltSerS3Application>(*args)
}