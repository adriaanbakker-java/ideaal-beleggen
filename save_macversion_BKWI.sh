#!bash
# script om mac specifieke bestanden te bewaren

DIRTOP="/Users/abakker/Pakket/ideaal-beleggen/macversion_BKWI" 
DIRBEL=${DIRTOP}"/Beleggingspakket"
DIRIDEA=${DIRBEL}"/idea"
set -x
rm -r ${DIRTOP}

 
 
mkdir ${DIRTOP}
mkdir ${DIRBEL}
mkdir ${DIRIDEA}
cp Beleggingspakket/src/main/java/beleggingspakket/Constants.java ${DIRBEL}
cp Beleggingspakket/*.iml ${DIRBEL}
cp Beleggingspakket/*.xml ${DIRBEL}
cp -r Beleggingspakket/.idea/* ${DIRIDEA}
# 
# 
