import com.mobilesolutionworks.test.TestClass
import org.gradle.util.VersionNumber

def call() {
    def object = new TestClass()
    println(object)

    try {
        def version = VersionNumber.parse("1.0.0-pa-0")
        println("version = " + version)
        println("""
        |${version.major}
        |${version.minor}
        |${version.patch}
        |${version.qualifier}""".stripMargin())
    } catch (exception) {
        exception.printStackTrace()
    }
}