DESCRIPTION = "Eclipse Kanto - Software Update"

LICENSE = "EPL-2.0 | Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54cd967551e55d39f55006d3344c22fc"

SRC_URI = "git://github.com/eclipse-kanto/software-update;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "1.0.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/software-update"
GO_INSTALL = "${GO_IMPORT}/cmd/software-update"

require software-update.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','software-update.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${SU_SYSUNIT_DD}/software-update.service"
FILES:${PN} += "${SU_BIN_DD}/software-update"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${SU_CFG_DD}/software-update/config.json"

RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/software-update"
RPROVIDES:${PN} += "kanto/software-update"

do_install() {
  install -d "${D}/${SU_BIN_DD}"

  install -m 0755 "${GO_BUILD_BINDIR}/software-update" "${D}${SU_BIN_DD}/software-update"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${SU_SYSUNIT_DD}

    # software-update
    install -d ${D}${SU_CFG_DD}/software-update

    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${SU_CFG_DD}/software-update

    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${SU_SYSUNIT_DD}/software-update.service

    # fill in the software-update systemd service template with the custom configs provided
    sed -e 's,@SU_BIN_DD@,${SU_BIN_DD},g' \
        -e 's,@SU_CFG_DD@,${SU_CFG_DD},g' \
        -i ${D}${SU_SYSUNIT_DD}/software-update.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@SU_LOG_DD@,${SU_LOG_DD},g' \
        -e 's,@SU_STORAGE_DD@,${SU_STORAGE_DD},g' \
        -i ${D}${SU_CFG_DD}/software-update/config.json
  fi
}
