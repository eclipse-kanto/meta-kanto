DESCRIPTION = "Eclipse Kanto - System Metrics"

LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c"

SRC_URI = "git://github.com/eclipse-kanto/system-metrics;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "0.1.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/system-metrics"
GO_INSTALL = "${GO_IMPORT}/cmd/metrics"

require system-metrics.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','system-metrics.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${EK_SYSUNIT_DD}/system-metrics.service"
FILES:${PN} += "${EK_BIN_DD}/system-metrics"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${SM_CFG_DD}/system-metrics/config.json"


RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/system-metrics"
RPROVIDES:${PN} += "kanto/system-metrics"

do_install() {
  install -d "${D}/${SM_BIN_DD}"

  install -m 0755 "${GO_BUILD_BINDIR}/metrics" "${D}${SM_BIN_DD}/system-metrics"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${SM_SYSUNIT_DD}

    # system-metrics
    install -d ${D}${SM_CFG_DD}/system-metrics

    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${SM_CFG_DD}/system-metrics

    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${SM_SYSUNIT_DD}/system-metrics.service

    # fill in the system-metrics systemd service template with the custom configs provided
    sed -e 's,@SM_BIN_DD@,${SM_BIN_DD},g' \
        -e 's,@SM_CFG_DD@,${SM_CFG_DD}/system-metrics,g' \
        -i ${D}${SM_SYSUNIT_DD}/system-metrics.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@SM_CFG_DD@,${SM_CFG_DD}/system-metrics,g' \
        -i ${D}${SM_CFG_DD}/system-metrics/config.json
  fi

}

