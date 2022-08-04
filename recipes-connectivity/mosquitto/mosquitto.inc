FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://suite-connector.conf"

do_install:append() {
  echo "include_dir ${sysconfdir}/mosquitto/suite-connector.d" >> ${D}${sysconfdir}/mosquitto/mosquitto.conf
  install -d ${D}${sysconfdir}/mosquitto/suite-connector.d
  install -m 0644 ${WORKDIR}/suite-connector.conf ${D}${sysconfdir}/mosquitto/suite-connector.d
}
