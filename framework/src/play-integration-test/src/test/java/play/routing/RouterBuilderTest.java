/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.routing;

import org.junit.Test;
import play.api.routing.Router;
import play.libs.F;
import play.mvc.Result;
import play.mvc.Results;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

/**
 * This class is in the integration tests so that we have the right helper classes to build a request with to test it.
 */
public class RouterBuilderTest {

    @Test
    public void noParameters() {
        Router router = new RouterBuilder()
                .GET("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void oneParameter() {
        Router router = new RouterBuilder()
                .GET("/hello/:to").routeTo(new F.Function<String, Result>() {
                    public Result apply(String to) {
                        return Results.ok("Hello " + to);
                    }
                })
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void twoParameters() {
        Router router = new RouterBuilder()
                .GET("/:say/:to").routeTo((say, to) -> Results.ok(say + " " + to))
                .build();

        assertThat(makeRequest(router, "GET", "/Hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/foo"));
    }

    @Test
    public void threeParameters() {
        Router router = new RouterBuilder()
                .GET("/:say/:to/:extra").routeTo((say, to, extra) -> Results.ok(say + " " + to + extra))
                .build();

        assertThat(makeRequest(router, "GET", "/Hello/world/!"), equalTo("Hello world!"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void noParametersAsync() {
        Router router = new RouterBuilder()
                .GET("/hello/world").routeAsync(() -> F.Promise.pure(Results.ok("Hello world")))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void oneParameterAsync() {
        Router router = new RouterBuilder()
                .GET("/hello/:to").routeAsync(to -> F.Promise.pure(Results.ok("Hello " + to)))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void twoParametersAsync() {
        Router router = new RouterBuilder()
                .GET("/:say/:to").routeAsync((say, to) -> F.Promise.pure(Results.ok(say + " " + to)))
                .build();

        assertThat(makeRequest(router, "GET", "/Hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/foo"));
    }

    @Test
    public void threeParametersAsync() {
        Router router = new RouterBuilder()
                .GET("/:say/:to/:extra").routeAsync((say, to, extra) -> F.Promise.pure(Results.ok(say + " " + to + extra)))
                .build();

        assertThat(makeRequest(router, "GET", "/Hello/world/!"), equalTo("Hello world!"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void get() {
        Router router = new RouterBuilder()
                .GET("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "POST", "/hello/world"));
    }

    @Test
    public void head() {
        Router router = new RouterBuilder()
                .HEAD("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "HEAD", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "POST", "/hello/world"));
    }

    @Test
    public void post() {
        Router router = new RouterBuilder()
                .POST("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "POST", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/hello/world"));
    }

    @Test
    public void put() {
        Router router = new RouterBuilder()
                .PUT("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "PUT", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "POST", "/hello/world"));
    }

    @Test
    public void delete() {
        Router router = new RouterBuilder()
                .DELETE("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "DELETE", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "POST", "/hello/world"));
    }

    @Test
    public void patch() {
        Router router = new RouterBuilder()
                .PATCH("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "PATCH", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "POST", "/hello/world"));
    }

    @Test
    public void options() {
        Router router = new RouterBuilder()
                .OPTIONS("/hello/world").routeTo(() -> Results.ok("Hello world"))
                .build();

        assertThat(makeRequest(router, "OPTIONS", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "POST", "/hello/world"));
    }

    @Test
    public void starMatcher() {
        Router router = new RouterBuilder()
                .GET("/hello/*to").routeTo((to) -> Results.ok("Hello " + to))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/blah/world"), equalTo("Hello blah/world"));
        assertNull(makeRequest(router, "GET", "/foo/bar"));
    }

    @Test
    public void regexMatcher() {
        Router router = new RouterBuilder()
                .GET("/hello/$to<[a-z]+>").routeTo((to) -> Results.ok("Hello " + to))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertNull(makeRequest(router, "GET", "/hello/10"));
    }
    
    @Test
    public void multipleRoutes() {
        Router router = new RouterBuilder()
                .GET("/hello/:to").routeTo((to) -> Results.ok("Hello " + to))
                .GET("/foo/bar").routeTo(() -> Results.ok("foo bar"))
                .POST("/hello/:to").routeTo((to) -> Results.ok("Post " + to))
                .GET("/*path").routeTo((path) -> Results.ok("Path " + path))
                .build();

        assertThat(makeRequest(router, "GET", "/hello/world"), equalTo("Hello world"));
        assertThat(makeRequest(router, "GET", "/foo/bar"), equalTo("foo bar"));
        assertThat(makeRequest(router, "POST", "/hello/world"), equalTo("Post world"));
        assertThat(makeRequest(router, "GET", "/something/else"), equalTo("Path something/else"));
    }

    @Test
    public void encoding() {
        Router router = new RouterBuilder()
                .GET("/simple/:to").routeTo((to) -> Results.ok("Simple " + to))
                .GET("/path/*to").routeTo((to) -> Results.ok("Path " + to))
                .GET("/regex/$to<.*>").routeTo((to) -> Results.ok("Regex " + to))
                .build();

        assertThat(makeRequest(router, "GET", "/simple/dollar%24"), equalTo("Simple dollar$"));
        assertThat(makeRequest(router, "GET", "/path/dollar%24"), equalTo("Path dollar%24"));
        assertThat(makeRequest(router, "GET", "/regex/dollar%24"), equalTo("Regex dollar%24"));
    }
    
    private String makeRequest(Router router, String method, String path) {
        Result result = routeAndCall(router, fakeRequest(method, path));
        if (result == null) {
            return null;
        } else {
            return contentAsString(result);
        }
    }

}
