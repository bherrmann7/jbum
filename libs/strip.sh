
for each in *.jar
do
  mkdir strip
  cd strip
  unzip ../$each
  rm -rf META-INF
  jar -cf ../$each .
  cd ..
  rm -rf strip
done

