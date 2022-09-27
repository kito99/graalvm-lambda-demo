# CDK Project for GraalVM Lambda Demo

This project was initially created with the command:

```
cdk init app --language java
```

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

> NOTE: The Code in this project is for CDK 1.x (it has not been upgraded to 2.x yet)

> NOTE: Currently the stack uses an ARM Docker image and an ARM AWS Lambda container. It will work on an M1 Mac, but 
> it won't work on an x86 machine. To change to x86, uncomment the code for the Intel image in `InfrastructureStack.java` and comment out the code for the ARM image. 
> In my experience, x86 Docker images DO NOT work on M1 Macs.

## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!
