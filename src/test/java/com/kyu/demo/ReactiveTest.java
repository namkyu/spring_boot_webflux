package com.kyu.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ReactiveTest {


    // 문제가 없어도 switchIfEmpty 무조건 호출된다.
    @Test
    public void test() {
        Mono<String> result = Mono.just("Some payload").switchIfEmpty(asyncAlternative());
        System.out.println(result);
    }

    // switchIfEmpty 가 무조건 호출되는 것을 방지하기 위해 Mono.defer 사용
    @Test
    public void test2() {
        Mono<String> result = Mono.just("Some payload").switchIfEmpty(Mono.defer(() -> asyncAlternative()));
        System.out.println(result);
    }

    @Test
    public void test3() {
        Mono.just("some")
                .flatMap(x -> {
                    if (x.length() > 5) {
                        return Mono.just(x);
                    } else {
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(Mono.just("empty"))
                .subscribe(System.out::print);
    }

    @Test
    public void test4() {
        Flux<Integer> seq = Flux.just(1, 2, 3); // Integer 값을 발생하는 Flux 생성
        seq.subscribe(value -> System.out.println("데이터 : " + value)); // 구독
    }

    @Test
    public void test5() {
        Flux.just(1, 2, 3)
                .doOnNext(i -> System.out.println("doOnNext: " + i)) // doOnNext() 메서드는 Flux가 Subscriber에 next 신호를 발생할 때 호출된다.
                .subscribe(i -> System.out.println("Received: " + i));
    }

    @Test
    public void test6() {
        Flux<Integer> seq = Flux.just(1, 2, 3)
                .doOnNext(i -> System.out.println("doOnNext: " + i));

        System.out.println("시퀀스 생성");
        seq.subscribe(i -> System.out.println("Received: " + i));
    }

    @Test
    public void test7() {
        // null을 값으로 받으면 값이 없는 Mono
        Mono<Integer> seq1 = Mono.justOrEmpty(null); // complete 신호
        Mono<Integer> seq2 = Mono.justOrEmpty(1); // next(1) 신호- complete 신호

        // Optional을 값으로 받음
        Mono<Integer> seq3 = Mono.justOrEmpty(Optional.empty()); // complete 신호
        Mono<Integer> seq4 = Mono.justOrEmpty(Optional.of(1)); // next(1) 신호 - complete 신호
    }

    // map() 한 개의 데이터를 1-1 방식으로 변환해준다.
    // 자바 스트림의 map()과 유사하다.
    @Test
    public void test8() {
        Flux.just("a", "bc", "def", "wxyz")
                .map(str -> str.length()) // 문자열을 Integer 값으로 1-1 변환
                .subscribe(len -> System.out.println(len));
    }

    // flatMap은 1개의 데이터로부터 시퀀스를 생성할 때 사용한다.
    // 즉 1-n 방식의 변환을 처리한다.
    // flatMap에 전달한 함수가 생성하는 각 Flux는 하나의 시퀀스처럼 연결된다.
    // 그래서 flatMap()의 결과로 생성되는 Flux의 타입이 Flux<Flux<Integer>>가 아니라 Flux<Integer>이다.
    @Test
    public void test9() {
        Flux<Integer> seq = Flux.just(1, 2, 3)
                .flatMap(i -> Flux.range(1, i)); // Integer를 Flux<Integer>로 1-N 변환
        seq.subscribe(System.out::println);
    }

    // filter()를 이용해서 시퀀스가 생성한 데이터를 걸러낼 수 있다.
    // filter()에 전달한 함수의 결과가 true인 데이터만 전달하고 false인 데이터는 발생하지 않는다.
    // 다음은 1부터 10 사이의 숫자 중에서 2로 나눠 나머지가 0인 (즉 짝수인) 숫자만 걸러내는 예를 보여준다.
    @Test
    public void test10() {
        Flux.range(1, 10)
                .filter(num -> num % 2 == 0)
                .subscribe(x -> System.out.print(x + " -> "));
    }

    // 시퀀스에 데이터가 없을 때 특정 값을 기본으로 사용하고 싶다면 defaultIfEmpty() 메서드를 사용하면 된다.
    // Mono와 Flux 모두 defaultIfEmpty()를 제공한다. 아래 코드는 사용 예이다.
    @Test
    public void test11() {
        Flux<String> recItems = getPopularItems().defaultIfEmpty("1");
        recItems.subscribe(o -> System.out.println(o));
    }

    // 시퀀스에 값이 없을 때 다른 시퀀스를 사용하고 싶다면 switchIfEmpty() 메서드를 사용
    @Test
    public void test12() {
        Flux<String> recItems = getPopularItems().switchIfEmpty(Flux.just("1", "2"));
        recItems.subscribe(s -> System.out.println(s));
    }

    // concatWith() 메서드를 사용하면 여러 시퀀스를 순서대로 연결할 수 있다.
    // concatWith로 연결한 시퀀스는 이전 시퀀스가 종료된 뒤에 구독을 시작한다.
    // 위 예에서는 seq1이 종료된 뒤에 seq2 구독을 시작하고 seq2가 종료된 뒤에 seq3 구독을 시작한다.
    @Test
    public void test13() {
        Flux<Integer> seq1 = Flux.just(1, 2, 3);
        Flux<Integer> seq2 = Flux.just(4, 5, 6);
        Flux<Integer> seq3 = Flux.just(7, 8, 9);
        seq1.concatWith(seq2).concatWith(seq3).subscribe(System.out::println);
    }

    // zipWith()를 사용하면 두 시퀀스의 값을 묶은 값 쌍을 생성하는 시퀀스를 생성할 수 있다.
    @Test
    public void test14() {
        Flux<String> tick1 = Flux.interval(Duration.ofSeconds(1)).map(tick -> tick + "초틱");
        Flux<String> tick2 = Flux.interval(Duration.ofMillis(700)).map(tick -> tick + "밀리초틱");
        tick1.zipWith(tick2).subscribe(tup -> System.out.println(tup));
    }

    @Test
    public void test15() {
        Flux.range(1, 10)
                .map(x -> {
                    if (x == 5) throw new RuntimeException("exception"); // 에러 발생
                    else return x;
                })
                .subscribe(
                        i -> System.out.println(i), // next 신호 처리
                        ex -> System.err.println(ex.getMessage()), // error 신호 처리
                        () -> System.out.println("complete") // complete 신호 처리
                );
    }

    // 에러가 발생할 때 에러 대신에 특정 값을 발생하고 싶다면 onErrorReturn() 메서드를 사용한다.
    @Test
    public void test16() {
        Flux<Integer> seq = Flux.range(1, 10)
                .map(x -> {
                    if (x == 5) throw new RuntimeException("exception");
                    else return x;
                })
                .onErrorReturn(-1);

        seq.subscribe(System.out::println);
    }

    // 에러 발생하면 다른 신호(시퀀스)나 다른 에러로 대체하기: onErrorResume
    @Test
    public void test17() {
        Random random = new Random();
        Flux<Integer> seq = Flux.range(1, 10)
                .map(x -> {
                    int rand = random.nextInt(8);
                    if (rand == 0) throw new IllegalArgumentException("illarg");
                    if (rand == 1) throw new IllegalStateException("illstate");
                    if (rand == 2) throw new RuntimeException("exception");
                    return x;
                })
                .onErrorResume(error -> {
                    if (error instanceof IllegalArgumentException) {
                        return Flux.just(21, 22);
                    }
                    if (error instanceof IllegalStateException) {
                        return Flux.just(31, 32);
                    }
                    return Flux.error(error);
                });

        seq.subscribe(System.out::println);
    }

    // 에러를 다른 에러로 변환하기: onErrorMap
    @Test
    public void test18() {
        Flux<Integer> intSeq = Flux.just(1, 2, 3);
        Flux<Integer> seq = intSeq.onErrorMap(error -> new RuntimeException());
        seq.subscribe(o -> System.out.println(o));
    }

    // retry()를 사용하면 에러가 발생했을 구독을 재시도할 수 있다.
    @Test
    public void test19() {
        Flux.range(1, 5)
                .map(input -> {
                    if (input < 4) return "num " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1) // 에러 신호 발생시 1회 재시도
                .subscribe(System.out::println, System.err::println);
    }

    @Test
    public void test20() {
        Flux.just(1, 2, 4, 5, 6)
                .log()
                .map(x -> x * 2)
                .subscribe(x -> log.info("next: {}", x));
    }

    // 시퀀스가 신호를 발생하는 과정에서 익셉션이 발생하면 어떻게 될까? 시퀀스가 여러 단게를 거쳐 변환한다면 어떤 시점에 익셉션이 발생했는지 단번에 찾기 힘들 수도 있다.
    @Test
    public void test21() {
        Flux.just(1, 2, 4, -1, 5, 6)
                .map(x -> x + 1)
                .checkpoint("MAP1")
                .map(x -> 10 / x) // 원본 데이터가 -1인 경우 x는 0이 되어 익셉션이 발생
                .checkpoint("MAP2")
                .subscribe(
                        x -> System.out.println("next: " + x),
                        err -> err.printStackTrace());
    }

    private Mono<String> asyncAlternative() {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            System.out.println("Hi there");
            return "Alternative";
        }));
    }

    private Flux<String> getPopularItems() {
        return Flux.empty();
    }

}
