#!bash
# script om mac specifieke bestanden te bewaren

DIRTOP="/Users/abakker/Pakket/ideaal-beleggen/macversion_BKWI" 
DIRBEL=${DIRTOP}"/Beleggingspakket"
DIRKOE=${DIRTOP}"/KoersenModule"
DIRIDEABEL=${DIRBEL}"/idea"
DIRIDEAKOE=${DIRKOE}"/idea"
set -x
rm -r ${DIRTOP}

 
 
mkdir ${DIRTOP}


mkdir ${DIRBEL}
mkdir ${DIRIDEABEL}
cp Beleggingspakket/src/main/java/beleggingspakket/Constants.java ${DIRBEL}
cp Beleggingspakket/*.iml ${DIRBEL}
cp Beleggingspakket/*.xml ${DIRBEL}
cp -r Beleggingspakket/.idea/* ${DIRIDEABEL}
# 
# 
mkdir ${DIRKOE}
mkdir ${DIRIDEAKOE}
cp KoersenModule/src/main/java/mypackage/Constants.java ${DIRKOE}
cp KoersenModule/*.iml ${DIRKOE}
cp KoersenModule/*.xml ${DIRKOE}
cp -r KoersenModule/.idea/* ${DIRIDEAKOE}
