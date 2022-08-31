# Contains configuration variables for Eclipse Kanto container management

# The destination directory of the container management config file
CM_CFG_DD ?= "${sysconfdir}"
# The destination directory of the  container management binaries
CM_BIN_DD ?= "${bindir}"
# The destination directory where the systemd service file for container management will be placed
CM_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the container management logs will be placed
CM_LOD_DD ?= "${localstatedir}/log"
# Enables the digital twin representation for container management
CM_THINGS_ENABLED ?= "${@bb.utils.contains('PACKAGECONFIG', 'cloudconn', 'true', 'false', d)}"
