# Common configuration variables to set the binaries and resources allocation directories for Eclipse Kanto file backup component
# The destination directory where the required configuration files for the proper connectivity establishment of the file backup will be placed
FB_CFG_DD ?= "${sysconfdir}"
# The destination directory where the generated service file will be placed
FB_SYSUNIT_DD ?= "${systemd_unitdir}/system"
# The destination directory where the file backup binaries will be placed
FB_BIN_DD ?= "${bindir}"
# The destination directory where the file backup logs will be placed
FB_LOG_DD ?= "${localstatedir}/log"
# The directory to be backed up
FB_BACKUP_DD ?= "${localstatedir}/tmp/file-backup"
# The access mode for the directory to be backed up or restored. Allowed values are: strict, scoped and lax.
FB_ACCESS_MODE ?= "scoped"
# Directory where backups and downloads will be stored
FB_STORAGE_DD ?= "${localstatedir}/lib"
