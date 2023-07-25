# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto update manager component
# The destination directory where the required configuration files for the proper connectivity establishment of the update manager will be placed
UM_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
UM_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the update manager binaries will be placed
UM_BIN_DD ?= "${bindir}"
# The destination directory where the update manager logs will be placed
UM_LOG_DD ?= "${localstatedir}/log"

