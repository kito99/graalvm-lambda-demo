package virtua.demo.graalvm.lambda;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.DockerImage;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApiProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;


@SuppressWarnings("unused")
public class InfrastructureStack extends Stack {

    public static final String HELLO_WORLD_JVM_PATH = "/hello-world-jvm";
    public static final String HELLO_WORLD_JVM_SNAPSTART_PATH = "/hello-world-jvm-snapstert";
    public static final String HELLO_WORLD_GRAAL_PATH = "/hello-world-graal";

    public InfrastructureStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public InfrastructureStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        HttpApi httpApi = new HttpApi(this, "GraalVmLambdaDemoApi", HttpApiProps.builder()
                .apiName("GraalVmLambdaDemoApi")
                .build());

        configureJvmFunction(httpApi);
        configureJvmSnapStartFunction(httpApi);
        configureGraalVmFunction(httpApi);
    }

    private void configureJvmFunction(HttpApi httpApi) {
        Function jvmHelloWorldFunction = new Function(this, "HelloWorldFunctionJvm", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../function/target/hello-world-lambda.jar"))
                .handler("virtua.demo.graalvm.lambda.HelloWorldRequestHandler")
                .memorySize(2048)
//                .logRetention(RetentionDays.ONE_WEEK)
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path(HELLO_WORLD_JVM_PATH)
                .methods(singletonList(HttpMethod.GET))
                .integration(new HttpLambdaIntegration("HelloWorldFunctionJvm", jvmHelloWorldFunction,
                        HttpLambdaIntegrationProps.builder()
                                .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                                .build()))
                .build());

        // Output the URL for the API to the console after creating the CloudFormation stack
        new CfnOutput(this, "HelloWorldJvmApiUrl", CfnOutputProps.builder()
                .exportName("HelloWorldJvmApiUrl")
                .value(httpApi.getApiEndpoint() + HELLO_WORLD_JVM_PATH)
                .build());
    }

    private void configureJvmSnapStartFunction(HttpApi httpApi) {
        Function jvmHelloWorldFunction = new Function(this, "HelloWorldFunctionJvmSnapStart", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../function/target/hello-world-lambda.jar"))
                .handler("virtua.demo.graalvm.lambda.HelloWorldRequestHandler")
                .memorySize(2048)
//                .logRetention(RetentionDays.ONE_WEEK)
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path(HELLO_WORLD_JVM_SNAPSTART_PATH)
                .methods(singletonList(HttpMethod.GET))
                .integration(new HttpLambdaIntegration("HelloWorldJvmSnapStartApiUrl", jvmHelloWorldFunction,
                        HttpLambdaIntegrationProps.builder()
                                .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                                .build()))
                .build());

        // Output the URL for the API to the console after creating the CloudFormation stack
        new CfnOutput(this, "HelloWorldJvmSnapStartApiUrl", CfnOutputProps.builder()
                .exportName("HelloWorldJvmSnapStartApiUrl")
                .value(httpApi.getApiEndpoint() + HELLO_WORLD_JVM_SNAPSTART_PATH)
                .build());
    }


    private void configureGraalVmFunction(@NotNull HttpApi httpApi) {
        List<String> functionOnePackagingInstructions = Arrays.asList(
                "-c",
                "mvn clean install -P native-image && cp /asset-input/target/function.zip /asset-output/ && ls -la /asset-input/target"
        );

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(functionOnePackagingInstructions)

                // x86 image
                // .image(DockerImage.fromRegistry("marksailes/al2-graalvm:al2-21.2.0"))

                // ARM image
                .image(DockerImage.fromRegistry("marksailes/arm64-al2-graalvm:11-22.2.0"))
                .volumes(singletonList(
                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);

        Function graalHelloWorldFunction = new Function(this, "HelloWorldFunctionGraalVm", FunctionProps.builder()
                .runtime(Runtime.PROVIDED_AL2)
                .code(Code.fromAsset("../function/", AssetOptions.builder()
                        .bundling(builderOptions.build())
                        .build()))
                .handler("virtua.demo.graalvm.lambda.HelloWorldRequestHandler")
                .memorySize(256)
//                .logRetention(RetentionDays.ONE_WEEK)

                // x86 deployment
                //.architectures(singletonList(Architecture.X86_64))

                // ARM deployment
                .architecture(Architecture.ARM_64)

                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path(HELLO_WORLD_GRAAL_PATH)
                .methods(singletonList(HttpMethod.GET))
                .integration(new HttpLambdaIntegration("HelloWorldGraalApiUrl", graalHelloWorldFunction,
                        HttpLambdaIntegrationProps.builder()
                                .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                                .build()))
                .build());

        // Output the URL for the API to the console after creating the CloudFormation stack
        new CfnOutput(this, "HelloWorldGraalApiUrl", CfnOutputProps.builder()
                .exportName("HelloWorldGraalApiUrl")
                .value(httpApi.getApiEndpoint() + HELLO_WORLD_GRAAL_PATH)
                .build());
    }


}
