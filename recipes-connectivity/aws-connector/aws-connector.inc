# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto suite connector component
# The destination directory where the required configuration files for the proper connectivity establishment of the suite connector will be placed
SC_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
SC_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the aws connector binaries will be placed
SC_BIN_DD ?= "${bindir}"
# The destination directory where the aws connector logs will be placed
SC_LOG_DD ?= "${localstatedir}/log"
