# Create a temporary directory
   mkdir gradle-wrapper-temp && cd gradle-wrapper-temp

   # Download the Gradle wrapper
   curl -O https://raw.githubusercontent.com/gradle/gradle/v7.6.2/gradle/wrapper/gradle-wrapper.jar
   curl -O https://raw.githubusercontent.com/gradle/gradle/v7.6.2/gradle/wrapper/gradle-wrapper.properties

   # Download the Gradle wrapper scripts
   curl -O https://raw.githubusercontent.com/gradle/gradle/v7.6.2/gradlew
   curl -O https://raw.githubusercontent.com/gradle/gradle/v7.6.2/gradlew.bat

   # Move back to the project root
   cd ..

   # Create gradle/wrapper directory if it doesn't exist
   mkdir -p gradle/wrapper

   # Move the downloaded files to the correct locations
   mv gradle-wrapper-temp/gradle-wrapper.jar gradle/wrapper/
   mv gradle-wrapper-temp/gradle-wrapper.properties gradle/wrapper/
   mv gradle-wrapper-temp/gradlew .
   mv gradle-wrapper-temp/gradlew.bat .

   # Make gradlew executable
   chmod +x gradlew

   # Clean up
   rm -rf gradle-wrapper-temp

   # Update the distributionUrl in gradle-wrapper.properties
   sed -i '' 's|distributionUrl=.*|distributionUrl=https\\://services.gradle.org/distributions/gradle-7.6.2-bin.zip|' gradle/wrapper/gradle-wrapper.properties

   echo "Gradle wrapper manually updated to version 7.6.2"
   