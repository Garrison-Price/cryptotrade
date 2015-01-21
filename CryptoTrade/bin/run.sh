#/bin/sh
OUT=$(./coinedup -j)
java -jar ../dist/CryptoTrade.jar $OUT $1
