package virtua.demo.graalvm.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

import java.util.Map;

public class HelloWorldRequestHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        System.out.println("Inside HelloWorldRequestHandler::handleRequest()");
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "text/html"))
                .withBody("Hello world!\r\n")
                .withStatusCode(200)
                .build();
    }
}
