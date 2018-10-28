# Loop through the line of version properties file and extract version
VERSION=""
while read properties
do
    if [[ $properties == version* ]];
        then
            VERSION=${properties:8}
            echo "Deploying version: " $VERSION
    fi
done < version.properties;

# This condition reach only if the last line of properties starts with "version"
if [[ $properties == version* ]];
    then
        VERSION=${properties:8}
        echo "Deploying version: " $VERSION
fi
