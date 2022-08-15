# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto software update component
# The destination directory where the required configuration files for the proper connectivity establishment of the software update will be placed
SU_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
SU_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the software update binaries will be placed
SU_BIN_DD ?= "${bindir}"
# The destination directory where the software update logs will be placed
SU_LOG_DD ?= "${localstatedir}/log"
# The destination directory where the software update storage will be placed
SU_STORAGE_DD ?= "${localstatedir}/lib"
