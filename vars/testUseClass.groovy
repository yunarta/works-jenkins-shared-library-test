import com.mobilesolutionworks.test.TestClass
import org.gradle.util.VersionNumber

def call() {
    VersionNumber a = VersionNumber.parse("1.0.0-pa-0")
    VersionNumber b = VersionNumber.parse("1.0.0-pa-2")
    def compare = a.compareTo(b)
    println("compare = ${compare}")

    def object = new TestClass()
    println("test = ${object}")
}