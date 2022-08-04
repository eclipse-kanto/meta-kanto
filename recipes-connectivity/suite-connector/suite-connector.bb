DESCRIPTION = "Eclipse Kanto - Suite Connector"

LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c"

SRC_URI = "git://github.com/eclipse-kanto/suite-connector;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

GO_IMPORT = "github.com/eclipse-kanto/suite-connector"
GO_INSTALL = "${GO_IMPORT}/cmd/connector"

require suite-connector.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','suite-connector.service','',d)}"

#workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${EK_SYSUNIT_DD}/suite-connector.service"
FILES:${PN} += "${EK_BIN_DD}/suite-connector"
#Ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${EK_CFG_DD}/suite-connector/config.json"
FILES:${PN} += "${EK_CFG_DD}/suite-connector/provisioning.json"
FILES:${PN} += "${EK_CFG_DD}/suite-connector/iothub.crt"


RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "edge/suite-connector"
RPROVIDES:${PN} += "edge/suite-connector"

do_install() {
  install -d "${D}/${EK_BIN_DD}"
 
  if  [ -f "${B}/bin/connector" ]; then
        install -m 0755 "${B}/bin/connector" "${D}${EK_BIN_DD}/suite-connector"
  fi
  
  #install -m 0755 "${B}/bin/linux_${GOARCH}/connector" "${D}${EK_BIN_DD}/suite-connector"
  if  [ -f "${B}/bin/linux_${GOARCH}/connector" ]; then
        install -m 0755 "${B}/bin/linux_${GOARCH}/connector" "${D}${EK_BIN_DD}/suite-connector"
  fi
  
  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
   install -d ${D}${EK_SYSUNIT_DD}
    
    # suite-connector
    install -d ${D}${EK_CFG_DD}/suite-connector
    
    # iothub.crt
    install -m 0644 ${S}/src/${GO_IMPORT}/cmd/connector/iothub.crt ${D}${EK_CFG_DD}/suite-connector
    
    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${EK_CFG_DD}/suite-connector
    
    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${EK_SYSUNIT_DD}/suite-connector.service
    
    # fill in the suite-connector systemd service template with the custom configs provided
    sed -e 's,@EK_BIN_DD@,${EK_BIN_DD},g' \
        -e 's,@EK_CFG_DD@,${EK_CFG_DD}/suite-connector,g' \
        -i ${D}${EK_SYSUNIT_DD}/suite-connector.service
    
    # fill in the config.json template with the custom configs provided
    sed -e 's,@EK_CFG_DD@,${EK_CFG_DD}/suite-connector,g' \
        -i ${D}${EK_CFG_DD}/suite-connector/config.json
  fi
  
  # provisioning.json
  if  [ -f "${COMMON_FILES}/provisioning.json" ]; then
    install -m 0644 ${COMMON_FILES}/provisioning.json ${D}${EK_CFG_DD}/suite-connector
  fi

}

