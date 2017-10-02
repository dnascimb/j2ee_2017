# Post Install Script For KSAML RPM
#!/bin/bash

VARDIR=/var

echo -n "${VERSION}" > ${VARDIR}/lib/kdms/webapps/ksaml/version.txt

cd ${VARDIR}/lib/kdms/webapps/ksaml
chown -R kdms:kdms *
chmod 775 ${VARDIR}/lib/kdms/webapps/ksaml/config
chmod 775 -R ${VARDIR}/lib/kdms/webapps/ksaml/logs


# We create a symbolic link to ksaml.xml, a file which is overwritten after the webapp is installed
ln -sf /var/lib/kdms/webapps/ksaml/config/ksaml.xml /etc/tomcat7/Catalina/localhost/
