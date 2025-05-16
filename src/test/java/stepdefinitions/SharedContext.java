package stepdefinitions;

import io.restassured.response.Response;

public class SharedContext {
    private Response latestResponse;

    public Response getLatestResponse() {
        return latestResponse;
    }

    public void setLatestResponse(Response response) {
        this.latestResponse = response;
    }
}
