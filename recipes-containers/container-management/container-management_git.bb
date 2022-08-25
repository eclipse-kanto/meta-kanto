DESCRIPTION = "Eclipse Kanto - Container Management"

LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = "file://src/github.com/eclipse-kanto/container-management/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c"

SRC_URI = "git://github.com/eclipse-kanto/container-management;protocol=https;branch=main \
           file://config.json \
           file://service.template \
           "

SRCREV = "${AUTOREV}"

PV = "0.1.0-git${SRCPV}"

GO_IMPORT = "github.com/eclipse-kanto/container-management"
GO_INSTALL = "${GO_IMPORT}/containerm/daemon \
             ${GO_IMPORT}/containerm/cli \
             "

require container-management.inc

inherit go-mod
inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','container-management.service','',d)}"

# workaround for network issue
do_compile[network] = "1"

# Different features of this package can be enabled depending on the target usage of the package.
inherit pkgconfig
# Currently the cloudconn feature is enabled by default. If you would like to disable this feature you can add
#             PACKAGECONFIG:remove:pn-container-management = "cloudconn"
# to your conf/local.conf file used for the build configuration of your image
# or you can configure it in a custom .bbappend file for example to disable it via adding
#             PACKAGECONFIG:remove = "cloudconn"
# into a .bbappend file to the original recipe.
PACKAGECONFIG ?= "cloudconn"
# This package feature enables cloud connectivity of the container management service via suite connector.
# It's enabled by default as container management is expected to work as an integrated component of the suite connector and not as a
# standalone service.
# Nevertheless, if this is required - it can be disabled on demand as shown above.
PACKAGECONFIG[cloudconn] = "--with-cloudconn,--without-cloudconn"

FILES:${PN} += "${CM_SYSUNIT_DD}/container-management.service"
FILES:${PN} += "${CM_BIN_DD}/container-management ${CM_BIN_DD}/kanto-cm"
# ensure all additional resources are properly packed in the resulting package if provided
FILES:${PN} += "${CM_CFG_DD}/container-management/config.json"

do_install() {
  install -d ${D}/${CM_BIN_DD}

  # kanto-cm
  install -m 0755 "${GO_BUILD_BINDIR}/cli" "${D}${CM_BIN_DD}/kanto-cm"

  # container-management
  install -m 0755 "${GO_BUILD_BINDIR}/daemon" "${D}${CM_BIN_DD}/container-management"

  if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then

    # config.json
    install -d ${D}${CM_CFG_DD}/container-management
    install -m 0644 ${WORKDIR}/config.json ${D}${CM_CFG_DD}/container-management/config.json

    # service.template as service
    install -d ${D}/${CM_SYSUNIT_DD}
    install -m 0644 ${WORKDIR}/service.template ${D}${CM_SYSUNIT_DD}/container-management.service

    # fill in the container management service template with the result configurations
    sed -e 's,@CM_BIN_DD@,${CM_BIN_DD},g' \
        -e 's,@CM_CFG_DD@,${CM_CFG_DD},g' \
    -i ${D}${CM_SYSUNIT_DD}/container-management.service

    # fill in the config.json template with the custom configs provided
    sed -e 's,@CM_LOD_DD@,${CM_LOD_DD},g' \
        -e 's,@CM_THINGS_ENABLED@,${CM_THINGS_ENABLED},g' \
        -i ${D}${CM_CFG_DD}/container-management/config.json

  fi
}

DEPENDS:append_class-target = " lvm2"
RDEPENDS:${PN} = "util-linux util-linux-unshare iptables \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'aufs', 'aufs-util', '', d)} \
                  ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '', 'cgroup-lite', d)} \
                  bridge-utils \
                  ca-certificates \
                 "

RDEPENDS:${PN} += "containerd-opencontainers runc-opencontainers"

RDEPENDS:${PN} += "kernel-module-dm-thin-pool kernel-module-nf-nat kernel-module-nf-conntrack-netlink kernel-module-xt-addrtype kernel-module-xt-masquerade"

KERNEL_MODULE_AUTOLOAD += "xt_conntrack xt_addrtype"

PROVIDES:${PN} += "kanto/container-management"
RPROVIDES:${PN} += "kanto/container-management"
