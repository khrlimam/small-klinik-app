# small-klinik-app
basic javafx app, utilizing guava eventbus as a bridge between components

## what apps do?
the app basically recording patient medical treatment.

## compile your self
#### first add traynotification.jar into your local repository
mvn install:install-file -Dfile=src/main/resources/TrayNotification.jar -DgroupId=tray.notification -DartifactId=traynotification -Dversion=0.0.1 -Dpackaging=jar

#### then install the app
mvn jfx:jar
