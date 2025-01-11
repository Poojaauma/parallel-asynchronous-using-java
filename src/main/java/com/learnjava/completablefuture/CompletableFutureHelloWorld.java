package com.learnjava.completablefuture;

import com.learnjava.service.HelloWorldService;

import java.util.concurrent.CompletableFuture;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.LoggerUtil.log;

public class CompletableFutureHelloWorld {
    HelloWorldService helloWorldService;

    public CompletableFutureHelloWorld(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    public CompletableFuture<String> helloWorld() {
        return CompletableFuture.supplyAsync(helloWorldService::helloWorld)
                .thenApply(String::toUpperCase);
//                .thenAccept((res) -> log("Result is : " + res))
//                .join();
    }

    public CompletableFuture<String> helloWorldWithCompose() {
        return CompletableFuture.supplyAsync(helloWorldService::hello)
                .thenCompose((prev) -> helloWorldService.worldFuture(prev))
                .thenApply(String::toUpperCase);
    }

    public CompletableFuture<String> helloWorld_withSize() {
        return CompletableFuture.supplyAsync(helloWorldService::helloWorld)
                .thenApply(String::toUpperCase)
                .thenApply((res -> res.length() + " - " + res));
    }
    public String helloWorldApproach1() {
        String hello = helloWorldService.hello();
        String world = helloWorldService.world();
        return hello + world;
    }

    public String helloWorldApproach2() {
        CompletableFuture<String> hello =
                CompletableFuture.supplyAsync(() ->helloWorldService.hello());
        CompletableFuture<String> world =
                CompletableFuture.supplyAsync(() ->helloWorldService.world());
        return hello.thenCombine(world, (h,w)-> h+w)
                .thenApply(String::toUpperCase).join();
    }

    public String helloWorldApproach3() {
        CompletableFuture<String> hello =
                CompletableFuture.supplyAsync(() ->helloWorldService.hello());
        CompletableFuture<String> world =
                CompletableFuture.supplyAsync(() ->helloWorldService.world());
        CompletableFuture<String> hi = CompletableFuture.supplyAsync(() -> {
            delay(1000);
            return " Hi CompletableFuture";
        });

        return hello.thenCombine(world, (h,w)-> h+w)
                .thenCombine(hi, (prev,curr)-> prev+curr)
                .thenApply(String::toUpperCase).join();
    }

    public String helloWorldApproach4() {
        CompletableFuture<String> hello =
                CompletableFuture.supplyAsync(() ->helloWorldService.hello());
        CompletableFuture<String> world =
                CompletableFuture.supplyAsync(() ->helloWorldService.world());
        CompletableFuture<String> hi = CompletableFuture.supplyAsync(() -> {
            delay(1000);
            return " Hi ";
        });
        CompletableFuture<String> there = CompletableFuture.supplyAsync(() -> {
            delay(1000);
            return "There";
        });

        return hello.thenCombine(world, (h,w)-> h+w)
                .thenCombine(hi, (prev,curr)-> prev+curr)
                .thenCombine(there, (prev,curr)-> prev+curr)
                .thenApply(String::toUpperCase).join();
    }

    public static void main(String[] args) {


        log("Done!");
        delay(2000);// To Avoid Main getting completed before the above
    }
}
