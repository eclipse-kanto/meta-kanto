FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://edge-agent.conf"

do_install:append() {
  echo "include_dir ${sysconfdir}/mosquitto/edge-agent.d" >> ${D}${sysconfdir}/mosquitto/mosquitto.conf
  install -d ${D}${sysconfdir}/mosquitto/edge-agent.d
  install -m 0644 ${WORKDIR}/edge-agent.conf ${D}${sysconfdir}/mosquitto/edge-agent.d
}
