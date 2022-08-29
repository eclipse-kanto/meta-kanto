DESCRIPTION = "Eclipse Kanto - File Backup"

LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c"

SRC_URI = "git://github.com/eclipse-kanto/file-backup;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "0.1.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/file-backup"
GO_INSTALL = "${GO_IMPORT}"

require file-backup.inc

inherit go-mod
inherit systemd

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','file-backup.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

FILES:${PN} += "${FB_SYSUNIT_DD}/file-backup.service"
FILES:${PN} += "${FB_BIN_DD}/file-backup"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${FB_CFG_DD}/file-backup/config.json"

RDEPENDS:${PN} += "mosquitto"

PROVIDES:${PN} += "kanto/file-backup"
RPROVIDES:${PN} += "kanto/file-backup"

do_install() {
  install -d "${D}/${FB_BIN_DD}"

  install -m 0755 "${GO_BUILD_BINDIR}/file-backup" "${D}${FB_BIN_DD}/file-backup"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
    install -d ${D}${FB_SYSUNIT_DD}

    # file-backup
    install -d ${D}${FB_CFG_DD}/file-backup

    # config.json
    install -m 0644 ${WORKDIR}/config.json ${D}${FB_CFG_DD}/file-backup

    # service.template as service
    install -m 0644 ${WORKDIR}/service.template ${D}${FB_SYSUNIT_DD}/file-backup.service

    # fill in the file-backup systemd service template with the custom configs provided
    sed -e 's,@FB_BIN_DD@,${FB_BIN_DD},g' \
        -e 's,@FB_CFG_DD@,${FB_CFG_DD},g' \
        -i ${D}${FB_SYSUNIT_DD}/file-backup.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@FB_LOG_DD@,${FB_LOG_DD},g' \
        -e 's,@FB_BACKUP_DD@,${FB_BACKUP_DD},g' \
        -i ${D}${FB_CFG_DD}/file-backup/config.json
  fi
}
