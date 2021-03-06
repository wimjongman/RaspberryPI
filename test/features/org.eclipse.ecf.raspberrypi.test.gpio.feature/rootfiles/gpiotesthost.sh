#!/bin/bash
JAVAPROG=java
JAVAPROPS="-Declipse.ignoreApp=true -Dosgi.noShutdown=true -Decf.generic.server.hostname=192.168.1.102"
echo "javaprops=${JAVAPROPS}"
EQUINOXJAR=plugins/org.eclipse.osgi_3.10.0.v20140407-2102.jar
echo "equinox=${EQUINOXJAR}"
PROGARGS="-configuration file:configuration -os linux -ws gtk -arch arm -console -consoleLog -debug"
${JAVAPROG} ${JAVAPROPS} -jar ${EQUINOXJAR} ${PROGARGS}
