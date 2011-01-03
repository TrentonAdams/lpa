#!/bin/bash
printf "/**\n" > /tmp/license-header.txt
cat license-header.txt | sed -e 's/^/ \* /' >> /tmp/license-header.txt
printf " */\n" >> /tmp/license-header.txt;

for FILE in $(find src/main/java -type f) $(find src/test/java -type f); do 
  cat /tmp/license-header.txt $FILE > $FILE.tmp; 
  mv $FILE.tmp $FILE; 
done

cat license-header.txt | sed -e 's/^/#  /' > /tmp/license-header.txt
for FILE in $(find src/test/resources -type f); do 
  cat /tmp/license-header.txt $FILE > $FILE.tmp; 
  mv $FILE.tmp $FILE; 
done
