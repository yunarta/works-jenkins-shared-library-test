import com.mobilesolutionworks.test.TestClass
import org.gradle.util.VersionNumber

def call() {
    def object = new TestClass()
    println(object)

    try {
        def a = VersionNumber.parse("1.0.0-pa-0")
        def b = VersionNumber.parse("1.0.0-pa-2")
        a.compareTo(b)
    } catch (exception) {
        exception.printStackTrace()
    }
}