# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto local digital twins component
# The destination directory where the required configuration files for the proper connectivity establishment of the local digital twins will be placed
LDT_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
LDT_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the local digital twins binaries will be placed
LDT_BIN_DD ?= "${bindir}"
# The destination directory where the local digital twins logs will be placed
LDT_LOG_DD ?= "${localstatedir}/log"
