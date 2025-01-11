package com.learnjava.completablefuture;

import com.learnjava.service.HelloWorldService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.timeTaken;
import static org.junit.jupiter.api.Assertions.*;
class CompletableFutureHelloWorldTest {
    HelloWorldService hws = new HelloWorldService();
    CompletableFutureHelloWorld completableFutureHelloWorld =
            new CompletableFutureHelloWorld(hws);

    @Test
    void helloWorld() {
        //given

        //when
        CompletableFuture<String> res = completableFutureHelloWorld.helloWorld();

        //then
        res.thenAccept(s-> {
            assertEquals("HELLO WORLD", s);
        }).join();
    }

    @Test
    void helloWorld_withSize() {
        //given

        //when
        CompletableFuture<String> res = completableFutureHelloWorld.helloWorld_withSize();

        //then
        res.thenAccept(s-> {
            assertEquals("11 - HELLO WORLD", s);
        }).join();
    }

    @Test
    void helloWorldApproach2() {
        String res = completableFutureHelloWorld.helloWorldApproach2();
        assertEquals("HELLO WORLD!", res);
    }

    @Test
    void helloWorldApproach3() {
        String res = completableFutureHelloWorld.helloWorldApproach3();
        assertEquals("HELLO WORLD! HI COMPLETABLEFUTURE", res);
    }

    @Test
    void helloWorldApproach4() {
        String res = completableFutureHelloWorld.helloWorldApproach4();
        assertEquals("HELLO WORLD! HI THERE", res);
    }

    @Test
    void helloWorldWithCompose() {
        //given
        startTimer();

        //when
        CompletableFuture<String> res = completableFutureHelloWorld.helloWorldWithCompose();

        //then
        res.thenAccept(s-> {
            assertEquals("HELLO WORLD!", s);
        }).join();
        timeTaken();
    }
}