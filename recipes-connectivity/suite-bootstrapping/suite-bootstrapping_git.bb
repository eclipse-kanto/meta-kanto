DESCRIPTION = "Eclipse Kanto - Suite bootstrapping Agent"

LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c"

SRC_URI = "git://github.com/eclipse-kanto/suite-bootstrapping;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

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

PROVIDES:${PN} += "edge/suite-bootstrapping"
RPROVIDES:${PN} += "edge/suite-bootstrapping"

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
        -e 's,@SB_CFG_DD@,${SB_CFG_DD}/suite-bootstrapping,g' \
        -i ${D}${SB_SYSUNIT_DD}/suite-bootstrapping.service
    
    # fill in the config.json template with the custom configs provided
    sed -e 's,@SB_CFG_DD@,${SB_CFG_DD}/suite-bootstrapping,g' \
        -i ${D}${SB_CFG_DD}/suite-bootstrapping/config.json
  fi
  
  # provisioning.json
  if  [ -f "${THISDIR}/files/provisioning.json" ]; then
    install -m 0644  ${THISDIR}/files/provisioning.json ${D}${SB_CFG_DD}/suite-bootstrapping
  fi
}

