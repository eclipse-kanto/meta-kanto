DESCRIPTION = "Eclipse Kanto - File Upload"

LICENSE = "EPL-2.0 | Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=54cd967551e55d39f55006d3344c22fc"

SRC_URI = "git://github.com/eclipse-kanto/file-upload;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "be9c7f236de2f2b64ca4fa58ceb1a95c4382b8b6"

PV = "1.0.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/file-upload"
GO_INSTALL = "${GO_IMPORT}"

require file-upload.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','file-upload.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${FU_SYSUNIT_DD}/file-upload.service"
FILES:${PN} += "${FU_BIN_DD}/file-upload"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${FU_CFG_DD}/file-upload/config.json"

RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/file-upload"
RPROVIDES:${PN} += "kanto/file-upload"

do_install() {
  install -d "${D}/${FU_BIN_DD}"

  install -m 0755 "${GO_BUILD_BINDIR}/file-upload" "${D}${FU_BIN_DD}/file-upload"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${FU_SYSUNIT_DD}

    # file-upload
    install -d ${D}${FU_CFG_DD}/file-upload

    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${FU_CFG_DD}/file-upload

    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${FU_SYSUNIT_DD}/file-upload.service

    # fill in the file-upload systemd service template with the custom configs provided
    sed -e 's,@FU_BIN_DD@,${FU_BIN_DD},g' \
        -e 's,@FU_CFG_DD@,${FU_CFG_DD},g' \
        -i ${D}${FU_SYSUNIT_DD}/file-upload.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@FU_LOG_DD@,${FU_LOG_DD},g' \
        -e 's,@FU_UPLOAD_DD@,${FU_UPLOAD_DD},g' \
        -i ${D}${FU_CFG_DD}/file-upload/config.json
  fi
}
