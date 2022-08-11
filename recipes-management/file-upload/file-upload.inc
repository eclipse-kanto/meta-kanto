# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto File Upload component
# The destination directory where the required configuration files for the proper connectivity establishment of the file upload will be placed
FU_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
FU_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the binaries will be placed
FU_BIN_DD ?= "${bindir}"
