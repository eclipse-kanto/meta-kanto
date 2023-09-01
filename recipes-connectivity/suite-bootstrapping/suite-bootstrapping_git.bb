DESCRIPTION = "Eclipse Kanto - Suite Bootstrapping"

LICENSE = "EPL-2.0 | Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54cd967551e55d39f55006d3344c22fc"

SRC_URI = "git://github.com/eclipse-kanto/suite-bootstrapping;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "d2aaa0104c69088c7c84fbeb0b55c35e30c6448c"

PV = "0.1.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/suite-bootstrapping"
GO_INSTALL = "${GO_IMPORT}/cmd/bootstrapping"

require suite-bootstrapping.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','suite-bootstrapping.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${SB_SYSUNIT_DD}/suite-bootstrapping.service"
FILES:${PN} += "${SB_BIN_DD}/suite-bootstrapping"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${SB_CFG_DD}/suite-bootstrapping/config.json"
FILES:${PN} += "${SB_CFG_DD}/suite-bootstrapping/provisioning.json"
FILES:${PN} += "${SB_CFG_DD}/suite-bootstrapping/iothub.crt"

RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/suite-bootstrapping"
RPROVIDES:${PN} += "kanto/suite-bootstrapping"

BUILD_VERSION = "0.1.0-M4"
export GO_EXTRA_LDFLAGS="-X main.version=${BUILD_VERSION}"

do_install() {
  install -d "${D}/${SB_BIN_DD}"
  
  install -m 0755 "${GO_BUILD_BINDIR}/bootstrapping" "${D}${SB_BIN_DD}/suite-bootstrapping"
  
  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${SB_SYSUNIT_DD}
    
    # suite-bootstrapping
    install -d ${D}${SB_CFG_DD}/suite-bootstrapping
    
    # iothub.crt
    install -m 0644 ${S}/src/${GO_IMPORT}/cmd/bootstrapping/iothub.crt ${D}${SB_CFG_DD}/suite-bootstrapping
    
    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${SB_CFG_DD}/suite-bootstrapping
    
    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${SB_SYSUNIT_DD}/suite-bootstrapping.service
    
    # fill in the suite-bootstrapping systemd service template with the custom configs provided
    sed -e 's,@SB_BIN_DD@,${SB_BIN_DD},g' \
        -e 's,@SB_CFG_DD@,${SB_CFG_DD},g' \
        -i ${D}${SB_SYSUNIT_DD}/suite-bootstrapping.service
    
    # fill in the config.json template with the custom configs provided
    sed -e 's,@SB_CFG_DD@,${SB_CFG_DD},g' \
        -e 's,@SB_LOG_DD@,${SB_LOG_DD},g' \
        -i ${D}${SB_CFG_DD}/suite-bootstrapping/config.json
  fi
  
  # provisioning.json
  if  [ -f "${THISDIR}/files/provisioning.json" ]; then
    install -m 0644  ${THISDIR}/files/provisioning.json ${D}${SB_CFG_DD}/suite-bootstrapping
  fi
}
