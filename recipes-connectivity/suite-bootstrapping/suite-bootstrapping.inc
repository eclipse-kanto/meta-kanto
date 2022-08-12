# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto bootstrapping component
# The destination directory where the required configuration files for the proper connectivity establishment of the bootstrapping agent will be placed
SB_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
SB_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the suite bootstrapping agent binaries will be placed
SB_BIN_DD ?= "${bindir}"
# The destination directory where the suite bootstrapping agent logs will be placed
SB_LOG_DD ?= "/var/log"
