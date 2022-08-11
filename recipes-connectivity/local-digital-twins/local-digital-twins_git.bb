DESCRIPTION = "Eclipse Kanto - Local Digital Twins"

LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c"

SRC_URI = "git://github.com/eclipse-kanto/local-digital-twins;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "0.1.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/local-digital-twins"
GO_INSTALL = "${GO_IMPORT}/cmd/twins"

require local-digital-twins.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','local-digital-twins.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${LDT_SYSUNIT_DD}/local-digital-twins.service"
FILES:${PN} += "${LDT_BIN_DD}/local-digital-twins"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${LDT_CFG_DD}/local-digital-twins/config.json"
FILES:${PN} += "${LDT_CFG_DD}/local-digital-twins/provisioning.json"
FILES:${PN} += "${LDT_CFG_DD}/local-digital-twins/iothub.crt"


RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/local-digital-twins"
RPROVIDES:${PN} += "kanto/local-digital-twins"

do_install() {
  install -d "${D}/${LDT_BIN_DD}"

  install -m 0755 "${GO_BUILD_BINDIR}/twins" "${D}${LDT_BIN_DD}/local-digital-twins"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${LDT_SYSUNIT_DD}

    # local-digital-twins
    install -d ${D}${LDT_CFG_DD}/local-digital-twins

    # iothub.crt
    install -m 0644 ${S}/src/${GO_IMPORT}/cmd/twins/iothub.crt ${D}${LDT_CFG_DD}/local-digital-twins

    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${LDT_CFG_DD}/local-digital-twins

    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${LDT_SYSUNIT_DD}/local-digital-twins.service

    # fill in the local-digital-twins systemd service template with the custom configs provided
    sed -e 's,@LDT_BIN_DD@,${LDT_BIN_DD},g' \
        -e 's,@LDT_CFG_DD@,${LDT_CFG_DD}/local-digital-twins,g' \
        -i ${D}${LDT_SYSUNIT_DD}/local-digital-twins.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@LDT_CFG_DD@,${LDT_CFG_DD}/local-digital-twins,g' \
        -i ${D}${LDT_CFG_DD}/local-digital-twins/config.json
  fi

  # provisioning.json
  if  [ -f "${THISDIR}/files/provisioning.json" ]; then
    install -m 0644  ${THISDIR}/files/provisioning.json ${D}/${LDT_CFG_DD}/local-digital-twins
  fi

}

