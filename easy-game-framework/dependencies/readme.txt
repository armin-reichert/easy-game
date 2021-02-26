The jar-file mp3libs.jar is  has to be added manually to your local Maven repository as follows:

mvn install:install-file -Dfile=mp3libs.jar -DgroupId=de.amr.easy.game -DartifactId=mp3libs -Dversion=1.0 -Dpackaging=jar
