# Hello World Function for GraalVM Lambda Demo

This is an ordinary Java Maven project, with the following targets:

* `mvn clean package` normal Java build
* `mvn clean package -P native-image` GraalVM Native Image build 
  * You need to have GraalVM installed and set as your default Java runtime for this to work.
    You can use [sdkman](https://sdkman.io/) or [jenv](https://www.jenv.be/) to manage your Java installations
    (there is already a `.java-version` file in this project, so jenv will pick it up automatically).

For the native image build, in addition to the additional Maven profile, you'll find [GraalVM rechability metadata](https://www.graalvm.org/22.2/reference-manual/native-image/metadata/) to support the 
AWS dependencies inside the [src/main/resources/META-INF/native-image](src/main/resources/META-INF/native-image) folder.

There is also the [src/main/config/bootstrap](src/main/config/bootstrap) file, which is required for the Lambda Custom Runtime. 
This project uses [aws-lambda-java-runtime-interface-client](https://github.com/aws/aws-lambda-java-libs/blob/master/aws-lambda-java-runtime-interface-client) to 
implement the Lambda [Runtime API](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html).
