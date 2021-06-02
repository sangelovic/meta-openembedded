SUMMARY = "C library and tools for interacting with the linux GPIO character device"
AUTHOR = "Bartosz Golaszewski <bgolaszewski@baylibre.com>"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRC_URI = " \
    https://www.kernel.org/pub/software/libs/${BPN}/${BP}.tar.xz \
    file://run-ptest \
"

SRC_URI[md5sum] = "28e79f6f70fee1da9079558d8b7b3736"
SRC_URI[sha256sum] = "841be9d788f00bab08ef22c4be5c39866f0e46cb100a3ae49ed816ac9c5dddc7"

inherit autotools pkgconfig python3native ptest

PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,kmod udev glib-2.0 catch2,bats python3-packaging"
PACKAGECONFIG[cxx] = "--enable-bindings-cxx,--disable-bindings-cxx"
PACKAGECONFIG[python3] = "--enable-bindings-python,--disable-bindings-python,python3"

# Enable cxx bindings by default.
PACKAGECONFIG ?= "cxx"

# Always build tools - they don't have any additional
# requirements over the library.
EXTRA_OECONF = "--enable-tools"

DEPENDS += "autoconf-archive-native"

PACKAGES =+ "${PN}-tools libgpiodcxx ${PN}-python"
FILES_${PN}-tools = " \
    ${bindir}/gpiodetect \
    ${bindir}/gpioinfo \
    ${bindir}/gpioget \
    ${bindir}/gpioset \
    ${bindir}/gpiofind \
    ${bindir}/gpiomon \
"
FILES_${PN}-ptest += " \
    ${bindir}/gpiod-test \
    ${bindir}/gpio-tools-test \
    ${bindir}/gpio-tools-test.bats \
    ${bindir}/gpiod-cxx-test \
    ${libdir}/libgpiomockup.so.* \
"
FILES_libgpiodcxx = "${libdir}/libgpiodcxx.so.*"
FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR}/*.so"
FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"

RRECOMMENDS_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'python3', '${PN}-python', '', d)}"

PACKAGECONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'tests', '', d)}"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp ${B}/tests/.libs/gpiod-test ${D}${PTEST_PATH}/tests/
}
