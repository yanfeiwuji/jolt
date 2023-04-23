package io.github.yanfeiwuji.jolt.ser.s3

import io.github.yanfeiwuji.jolt.core.JoltApi
import io.github.yanfeiwuji.jolt.core.JoltDao
import io.github.yanfeiwuji.jolt.core.JoltModel
import jakarta.persistence.Entity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author  yanfeiwuji
 * @date  2023/4/23 23:23
 */


@Entity
class S3Config : JoltModel() {
    // type
    var type: String? = null

    // config jsonStr
    var config: String? = null

}

interface S3ConfigDao : JoltDao<S3Config>

// controller
//
@RestController
@RequestMapping("s3/config")
class S3ConfigApi : JoltApi<S3Config>() {

}