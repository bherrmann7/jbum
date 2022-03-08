./gradlew shadowJar
cp  build/libs/jbum-all.jar /usr/local/bin/jbum.jar
scp build/libs/jbum-all.jar root@wilddog.local:/usr/local/bin/jbum.jar
