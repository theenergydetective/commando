#!/bin/bash
[ `whoami` = root ] || { sudo -s "$0" "$@"; exit $?; }

echo "Upgrading server for Commando installation"

service commando stop

rm -rf /opt/commando/lib/commando-1.0.2.jar
wget https://github.com/theenergydetective/commando/releases/latest/download/commander-1.0.2.jar -O /opt/commando/lib/commando-1.0.2.jar
echo "USE_START_STOP_DAEMON=false" > /opt/commando/lib/commando-1.0.conf
chmod 755 /opt/commando/lib/commando-1.0.2.jar

rm -rf /etc/init.d/commando
ln -s /opt/commando/lib/commando-1.0.2.jar /etc/init.d/commando
/bin/systemctl daemon-reload
update-rc.d commando defaults

service commando restart
