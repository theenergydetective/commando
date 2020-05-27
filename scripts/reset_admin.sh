#!/bin/bash
[ `whoami` = root ] || { sudo -s "$0" "$@"; exit $?; }

echo "Clearing the Commando admin password. Be sure to immediately log in and reset it."
service commando stop
sed -i "s/COMMANDO_PASSWORD=.*/COMMANDO_PASSWORD=/g" /opt/commando/data/commando.user.properties
service commando start

