DESCRIPTION = "Eclipse Kanto - Azure Connector"

LICENSE = "EPL-2.0 | Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54cd967551e55d39f55006d3344c22fc"

SRC_URI = "git://github.com/eclipse-kanto/azure-connector;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "1.0.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/azure-connector"
GO_INSTALL = "${GO_IMPORT}/cmd/azure-connector"

require azure-connector.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','azure-connector.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${SC_SYSUNIT_DD}/azure-connector.service"
FILES:${PN} += "${SC_BIN_DD}/azure-connector"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${SC_CFG_DD}/azure-connector/config.json"
FILES:${PN} += "${SC_CFG_DD}/azure-connector/iothub.crt"


RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/azure-connector"
RPROVIDES:${PN} += "kanto/azure-connector"

#BUILD_VERSION = "1.0.0"
#export GO_EXTRA_LDFLAGS="-X main.version=${BUILD_VERSION}"

do_install() {
  install -d "${D}/${SC_BIN_DD}"
 
  install -m 0755 "${GO_BUILD_BINDIR}/azure-connector" "${D}${SC_BIN_DD}/azure-connector"
  
  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${SC_SYSUNIT_DD}
    
    # azure-connector
    install -d ${D}${SC_CFG_DD}/azure-connector
    
    # iothub.crt
    install -m 0644 ${S}/src/${GO_IMPORT}/cmd/azure-connector/iothub.crt ${D}${SC_CFG_DD}/azure-connector
    
    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${SC_CFG_DD}/azure-connector
    
    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${SC_SYSUNIT_DD}/azure-connector.service
    
    # fill in the azure-connector systemd service template with the custom configs provided
    sed -e 's,@SC_BIN_DD@,${SC_BIN_DD},g' \
        -e 's,@SC_CFG_DD@,${SC_CFG_DD},g' \
        -i ${D}${SC_SYSUNIT_DD}/azure-connector.service
    
    # fill in the config.json template with the custom configs provided
    sed -e 's,@SC_CFG_DD@,${SC_CFG_DD},g' \
	    -e 's,@SC_LOG_DD@,${SC_LOG_DD},g' \
        -i ${D}${SC_CFG_DD}/azure-connector/config.json
  fi
}
