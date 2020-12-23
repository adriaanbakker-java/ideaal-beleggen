# JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.6.jdk/Contents/Home
if [ -d "/Library/Java/JavaVirtualMachines/jdk-11.0.6.jdk/Contents/Home" ]; then
   echo "Directory bkwi exists."
   JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.6.jdk/Contents/Home
fi
if [ -d "/c/Program Files/AdoptOpenJDK/jdk-11.0.6.10-hotspot" ]; then
   echo "Directory pc exists."
   JAVA_HOME="/c/Program Files/AdoptOpenJDK/jdk-11.0.6.10-hotspot"
fi
echo $JAVA_HOME

cd ./beleggingspakket/target
java -jar beleggingspakket-1.0.0-SNAPSHOT.jar
