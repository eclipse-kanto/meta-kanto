DESCRIPTION = "Eclipse Kanto - Suite Connector"

LICENSE = "EPL-2.0 | Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54cd967551e55d39f55006d3344c22fc"

SRC_URI = "git://github.com/eclipse-kanto/suite-connector;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "0.1.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/suite-connector"
GO_INSTALL = "${GO_IMPORT}/cmd/connector"

require suite-connector.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','suite-connector.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${SC_SYSUNIT_DD}/suite-connector.service"
FILES:${PN} += "${SC_BIN_DD}/suite-connector"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${SC_CFG_DD}/suite-connector/config.json"
FILES:${PN} += "${SC_CFG_DD}/suite-connector/provisioning.json"
FILES:${PN} += "${SC_CFG_DD}/suite-connector/iothub.crt"


RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/suite-connector"
RPROVIDES:${PN} += "kanto/suite-connector"

do_install() {
  install -d "${D}/${SC_BIN_DD}"
 
  install -m 0755 "${GO_BUILD_BINDIR}/connector" "${D}${SC_BIN_DD}/suite-connector"
  
  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${SC_SYSUNIT_DD}
    
    # suite-connector
    install -d ${D}${SC_CFG_DD}/suite-connector
    
    # iothub.crt
    install -m 0644 ${S}/src/${GO_IMPORT}/cmd/connector/iothub.crt ${D}${SC_CFG_DD}/suite-connector
    
    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${SC_CFG_DD}/suite-connector
    
    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${SC_SYSUNIT_DD}/suite-connector.service
    
    # fill in the suite-connector systemd service template with the custom configs provided
    sed -e 's,@SC_BIN_DD@,${SC_BIN_DD},g' \
        -e 's,@SC_CFG_DD@,${SC_CFG_DD},g' \
        -i ${D}${SC_SYSUNIT_DD}/suite-connector.service
    
    # fill in the config.json template with the custom configs provided
    sed -e 's,@SC_CFG_DD@,${SC_CFG_DD},g' \
	    -e 's,@SC_LOG_DD@,${SC_LOG_DD},g' \
        -i ${D}${SC_CFG_DD}/suite-connector/config.json
  fi
  
  # provisioning.json
  if  [ -f "${THISDIR}/files/provisioning.json" ]; then
    install -m 0644  ${THISDIR}/files/provisioning.json ${D}/${SC_CFG_DD}/suite-connector
  fi

}

