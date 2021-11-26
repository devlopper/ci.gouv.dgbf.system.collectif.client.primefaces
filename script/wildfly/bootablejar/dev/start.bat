call set_keycloak_variable.bat
mvn -f ../../../.. clean wildfly-jar:dev -P package.wildfly.bootable.jsf,dev