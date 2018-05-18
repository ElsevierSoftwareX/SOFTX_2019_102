echo This will configure Maven. Make sure maven is on path

mvn install:install-file -Dfile=monetdb-jdbc-2.18.jar -DgroupId=coppe -DartifactId=monetdb-jdbc -Dversion=2.18 -D packaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=ant-1.9.4.jar -DgroupId=coppe -DartifactId=ant -Dversion=1.9.4 -D packaging=jar -DgeneratePom=true

echo done