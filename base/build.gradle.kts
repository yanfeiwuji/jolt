dependencies {
    implementation(project(":core"))
    // keycloak client sdk
    implementation("org.keycloak:keycloak-admin-client:21.0.2")
    implementation("org.graalvm.js:js:22.3.2")
    implementation("org.graalvm.js:js-scriptengine:22.3.2")

//    nativeBuild {
//        sourceSets {
//            main {
//                resources {
////                    resources.srcDir("${rootDir}/core/src/main/resources")
//                    include("**/*.yaml")
//                }
//            }
//        }
//    }

}