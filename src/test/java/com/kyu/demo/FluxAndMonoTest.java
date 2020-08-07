package com.kyu.demo;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @Project : spring_boot_webflux
 * @Date : 2020-08-06
 * @Author : nklee
 * @Description :
 */
public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux.just("spring", "spring boot", "reactive spring")
//                .concatWith(Flux.error(new RuntimeException(("Exception Occurred"))))
                .concatWith(Flux.just("After Error"))
                .log() // subscriber 내부 동작을 로그로 확인할 수 있다.
                .subscribe(System.out::println, e -> System.err.println(e), () -> System.out.println("Completed"));
    }

    @Test
    public void monoTest() {
        Mono<String> springMono = Mono.just("Spring");
        StepVerifier.create(springMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxUsingIterable() {
        List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");
        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"adam", "anna", "jack", "jenny"};
        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<Object> mono = Mono.justOrEmpty(null);
        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "adam";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        StepVerifier.create(integerFlux.log())
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }
}
