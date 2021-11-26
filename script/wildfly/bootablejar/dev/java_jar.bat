call set_keycloak_variable.bat
call mvn -f ../../../.. clean package -Dwildfly.bootable.hollow=true -P package.war,package.wildfly.bootable.jsf,dev
call java -jar ..\..\..\..\target\acteur-bootable.jar --deployment=..\..\..\..\target\acteur.war -Djboss.http.port=8082