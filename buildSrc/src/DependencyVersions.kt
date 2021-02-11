@file:Suppress("unused")

fun kotest(module: String) = "io.kotest:kotest-$module:4.3.2"
fun arrow(module: String) = "io.arrow-kt:arrow-$module:0.11.0"
fun smack(module: String) = "org.igniterealtime.smack:smack-$module:4.3.4"
fun jxmpp(module: String) = "org.jxmpp:jxmpp-$module:0.6.4"
private const val mockkVersion = "1.10.5"
fun mockk(module: String) = "io.mockk:mockk-$module:$mockkVersion"
const val mockk = "io.mockk:mockk:$mockkVersion"
