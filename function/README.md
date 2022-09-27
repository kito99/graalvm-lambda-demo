# Hello World Function for GraalVM Lambda Demo

This is an ordinary Java Maven project, with the following targets:

* `mvn clean package` normal Java build
* `mvn clean package -P native-image` GraalVM Native Image build

For the native image build, in addition to the additional Maven profile, you'll find GraalVM metadata to support the 
AWS dependencies inside the [src/main/resources/META-INF/native-image](src/main/resources/META-INF/native-image) folder.

There is also the [src/main/config/bootstrap](src/main/config/bootstrap) file, which is required for the Lambda Custom Runtime. 
This project uses [aws-lambda-java-runtime-interface-client](https://github.com/aws/aws-lambda-java-libs/blob/master/aws-lambda-java-runtime-interface-client) to 
implement the Lambda [Runtime API](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html).
