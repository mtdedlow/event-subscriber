# Event Subscriber sample


MTD: 
keytool -importcert -alias prismsoaroot -file itbs.pem -keystore truststore.jks



This sample Java application receives events from the OpenAccess Web Event Bridge. The sample builds
with Gradle (http://gradle.org/).

## Configuration

The OpenAccess service URL, login credentials, and other parameters are defined in
**src/main/java/Program.java**. Update these parameters to reflect your environment.

## Building

1. Install the Java Development Kit.
2. Execute `gradlew build` at a command prompt. The first time you run this command, Gradle and the
   Java dependencies are downloaded. If you are behind a proxy, you might need update the
   **gradle.properties** file with the correct proxy information. Uncomment each line by removing
   the `#` and specify the proxy host and port. Update all four lines to set the proxy for both HTTP
   and HTTPS protocols.

## Running

1. Make sure the root certificate of the SSL certificate is installed in the Java **cacerts**
   certificate store, making the SSL connection to OpenAccess trusted.
    1. If using the default SSL certificate, export the root **Prism SOA Common Trusted Root**
       certificate from the **Trusted Root Certification Authorities** store of the local computer
       using **Microsoft Management Console**. Export the certificate with either DER or Base-64
       encoding.
    2. Run a command like the following, which adds the exported certificate to the Java certificate
       store. This will depend on the version of the JRE you are using. You will need to enter a
       password, which is usually `changeit` or `changeme` by default, depending on the environment.
       ```
       "c:\Program Files\Java\jdk1.8.0_65\jre\bin\keytool.exe"
       -importcert -alias prismsoaroot -file "F:\Certificates\PrismSOARoot.cer"
       -keystore "C:\Program Files\Java\jdk1.8.0_65\jre\lib\security\cacerts"
       ```
2. Execute `gradlew run`, or extract one of the archives in **build\distributions** (created by
   `gradlew build`) and execute the appropriate startup script in the **bin** directory. If you run
   the sample with Gradle, the sample output will be contained within the Gradle output, which can
   be confusing if you are not familiar with it. For example, you will see something like
   `Building 75% > :run` on the last line of output while the sample is running. This indicates that
   the current Gradle task being executed is the `run` task. The sample is listening for events as
   soon as it prints `Connection to message bus established`. Press [Enter] to exit the sample. Note
   that `gradlew run` uses the JDK's private JRE (probably
   **C:\Program Files\Java\jdk1.8.0_65\jre**). Running the build output in **build\distributions**
   uses the public JRE in the path (probably **C:\Program Files\Java\jre1.8.0_65**), as expected.
