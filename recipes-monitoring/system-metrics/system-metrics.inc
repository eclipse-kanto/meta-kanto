# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto System Metrics component
# The destination directory where the required configuration files for the proper connectivity establishment of the system metrics will be placed
SM_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
SM_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the binaries will be placed
SM_BIN_DD ?= "${bindir}"
# The destination directory where the logs will be placed
SM_LOG_DD ?= "${localstatedir}/log"