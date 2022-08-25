# Contains configuration variables for Eclipse Kanto container management

# The destination directory of the containers management config file
CM_CFG_DD ?= "${sysconfdir}"
# The destination directory of the  containers management binaries
CM_BIN_DD ?= "${bindir}"
# The destination directory where the systemd service file for container management will be placed
CM_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# Additional systemd units that are required by container management
CM_SRV_REQ ?= "${@bb.utils.contains('PACKAGECONFIG', 'cloudconn', ' suite-connector.service', '',d)}"
# Additional systemd units that containers management must be started after
CM_SRV_AFTER ?= "${@bb.utils.contains('PACKAGECONFIG', 'cloudconn', ' suite-connector.service', '',d)}"
# The destination directory where the container management logs will be placed
CM_LOD_DD ?= "${localstatedir}/log"
# Controls cloud connectivity for containers via suite connector
CM_THINGS_ENABLED ?= "${@bb.utils.contains('PACKAGECONFIG', 'cloudconn', 'true', 'false', d)}"
