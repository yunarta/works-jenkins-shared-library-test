package org.gradle.util

class VersionNumber implements Comparable<VersionNumber>, Serializable {
    static def parse(String versionString) {
        return "ABC"
//        return VersionNumber(1, 2, 3, "alpha-1")
//        return DEFAULT_SCHEME.parse(versionString)
    }

    @Override
    int compareTo(VersionNumber o) {
        return 0
    }
}