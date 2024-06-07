DESCRIPTION = "Eclipse Kanto - Update Manager"

LICENSE = "EPL-2.0 | Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54cd967551e55d39f55006d3344c22fc"

SRC_URI = "git://github.com/eclipse-kanto/update-manager;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "0b23c81367480892efd99c9fb086bad135d2e9e2"

PV = "1.0.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/update-manager"
GO_INSTALL = "${GO_IMPORT}/cmd/update-manager"

require update-manager.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','kanto-update-manager.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${UM_SYSUNIT_DD}/kanto-update-manager.service"
FILES:${PN} += "${UM_BIN_DD}/kanto-update-manager"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${UM_CFG_DD}/kanto-update-manager/config.json"

RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/update-manager"
RPROVIDES:${PN} += "kanto/update-manager"

# BUILD_VERSION = "0.1.0-M2"
# export GO_EXTRA_LDFLAGS="-X main.version=${BUILD_VERSION}"

do_install() {
  install -d "${D}/${UM_BIN_DD}"

  install -m 0755 "${GO_BUILD_BINDIR}/update-manager" "${D}${UM_BIN_DD}/kanto-update-manager"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${UM_SYSUNIT_DD}

    # update-manager
    install -d ${D}${UM_CFG_DD}/kanto-update-manager

    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${UM_CFG_DD}/kanto-update-manager

    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${UM_SYSUNIT_DD}/kanto-update-manager.service

    # fill in the update-manager systemd service template with the custom configs provided
    sed -e 's,@UM_BIN_DD@,${UM_BIN_DD},g' \
        -e 's,@UM_CFG_DD@,${UM_CFG_DD},g' \
        -i ${D}${UM_SYSUNIT_DD}/kanto-update-manager.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@UM_LOG_DD@,${UM_LOG_DD},g' \
        -i ${D}${UM_CFG_DD}/kanto-update-manager/config.json
  fi
}
