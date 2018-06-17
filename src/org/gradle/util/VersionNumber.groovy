package org.gradle.util

class VersionNumber implements Comparable<VersionNumber> {
    private static final DefaultScheme DEFAULT_SCHEME = new DefaultScheme()
    private static final SchemeWithPatchVersion PATCH_SCHEME = new SchemeWithPatchVersion()
    public static final VersionNumber UNKNOWN = version(0)

    private final int major
    private final int minor
    private final int micro
    private final int patch
    private final String qualifier
    private final AbstractScheme scheme

    public VersionNumber(int major, int minor, int micro, String qualifier) {
        this(major, minor, micro, 0, qualifier, DEFAULT_SCHEME)
    }

    public VersionNumber(int major, int minor, int micro, int patch, String qualifier) {
        this(major, minor, micro, patch, qualifier, PATCH_SCHEME)
    }

    private VersionNumber(int major, int minor, int micro, int patch,
                          String qualifier, AbstractScheme scheme) {
        this.major = major
        this.minor = minor
        this.micro = micro
        this.patch = patch
        this.qualifier = qualifier
        this.scheme = scheme
    }

    int getMajor() {
        return major
    }

    int getMinor() {
        return minor
    }

    int getMicro() {
        return micro
    }

    int getPatch() {
        return patch
    }

    String getQualifier() {
        return qualifier
    }

    VersionNumber getBaseVersion() {
        return new VersionNumber(major, minor, micro, patch, null, scheme)
    }

    int compareTo(VersionNumber other) {
        if (major != other.major) {
            return major - other.major
        }
        if (minor != other.minor) {
            return minor - other.minor
        }
        if (micro != other.micro) {
            return micro - other.micro
        }
        if (patch != other.patch) {
            return patch - other.patch
        }
        return Ordering.natural().nullsLast().compare(toLowerCase(qualifier), toLowerCase(other.qualifier))
    }

    boolean equals(Object other) {
        return other instanceof VersionNumber && compareTo((VersionNumber) other) == 0
    }

    int hashCode() {
        int result = major
        result = 31 * result + minor
        result = 31 * result + micro
        result = 31 * result + patch
        result = 31 * result + Objects.hashCode(qualifier)
        return result
    }

    String toString() {
        return scheme.format(this)
    }

    static VersionNumber version(int major) {
        return new VersionNumber(major, 0, 0, 0, null, DEFAULT_SCHEME)
    }

    /**
     * Returns the default MAJOR.MINOR.MICRO-QUALIFIER scheme.
     */
    static Scheme scheme() {
        return DEFAULT_SCHEME
    }

    /**
     * Returns the MAJOR.MINOR.MICRO.PATCH-QUALIFIER scheme.
     */
    static Scheme withPatchNumber() {
        return PATCH_SCHEME
    }

    static VersionNumber parse(String versionString) {
        return DEFAULT_SCHEME.parse(versionString)
    }

    private String toLowerCase(String string) {
        return string == null ? null : string.toLowerCase()
    }

    interface Scheme {

        VersionNumber parse(String value)

        String format(VersionNumber versionNumber)
    }

    private abstract static class AbstractScheme implements Scheme {
        final int depth

        protected AbstractScheme(int depth) {
            this.depth = depth
        }

        VersionNumber parse(String versionString) {
            if (versionString == null || versionString.length() == 0) {
                return UNKNOWN
            }
            Scanner scanner = new Scanner(versionString)

            int major = 0
            int minor = 0
            int micro = 0
            int patch = 0

            if (!scanner.hasDigit()) {
                return UNKNOWN
            }

            major = scanner.scanDigit()
            if (scanner.isSeparatorAndDigit('.' as char)) {
                scanner.skipSeparator()
                minor = scanner.scanDigit()
                if (scanner.isSeparatorAndDigit('.' as char)) {
                    scanner.skipSeparator()
                    micro = scanner.scanDigit()
                    if (depth > 3 && scanner.isSeparatorAndDigit('.' as char, '_' as char)) {
                        scanner.skipSeparator()
                        patch = scanner.scanDigit()
                    }
                }
            }

            if (scanner.isEnd()) {
                return new VersionNumber(major, minor, micro, patch, null, this)
            }

            if (scanner.isQualifier()) {
                scanner.skipSeparator()
                return new VersionNumber(major, minor, micro, patch, scanner.remainder(), this)
            }

            return UNKNOWN
        }

        private static class Scanner {
            int pos
            final String str

            private Scanner(String string) {
                this.str = string
            }

            boolean hasDigit() {
                return pos < str.length() && Character.isDigit(str.charAt(pos))
            }

            boolean isSeparatorAndDigit(char ... separators) {
                return pos < str.length() - 1 && oneOf(separators) && Character.isDigit(str.charAt(pos + 1))
            }

            private boolean oneOf(char ... separators) {
                char current = str.charAt(pos)
                for (int i = 0; i < separators.length; i++) {
                    char separator = separators[i]
                    if (current == separator) {
                        return true
                    }
                }
                return false
            }

            boolean isQualifier() {
                return pos < str.length() - 1 && oneOf('.' as char, '-' as char)
            }

            int scanDigit() {
                int start = pos
                while (hasDigit()) {
                    pos++
                }
                return Integer.parseInt(str.substring(start, pos))
            }

            boolean isEnd() {
                return pos == str.length()
            }

            private boolean skip(char ch) {
                if (pos < str.length() && str.charAt(pos) == ch) {
                    pos++
                    return true
                }
                return false
            }

            void skipSeparator() {
                pos++
            }

            String remainder() {
                return pos == str.length() ? null : str.substring(pos)
            }
        }
    }

    private static class DefaultScheme extends AbstractScheme {
        private static final String VERSION_TEMPLATE = "%d.%d.%d%s"

        DefaultScheme() {
            super(3)
        }

        String format(VersionNumber versionNumber) {
            return String.format(VERSION_TEMPLATE, versionNumber.major, versionNumber.minor, versionNumber.micro, versionNumber.qualifier == null ? "" : "-" + versionNumber.qualifier)
        }
    }

    private static class SchemeWithPatchVersion extends AbstractScheme {
        private static final String VERSION_TEMPLATE = "%d.%d.%d.%d%s"

        private SchemeWithPatchVersion() {
            super(4)
        }

        String format(VersionNumber versionNumber) {
            return String.format(VERSION_TEMPLATE, versionNumber.major, versionNumber.minor, versionNumber.micro, versionNumber.patch, versionNumber.qualifier == null ? "" : "-" + versionNumber.qualifier)
        }
    }

}