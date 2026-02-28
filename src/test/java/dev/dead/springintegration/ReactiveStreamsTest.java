package dev.dead.springintegration;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ReactiveStreamsTest {

    @Test
    public void createFlux() {
        Flux<Integer> integerFlux = Flux.fromStream(IntStream.range(0, 10)
                .boxed());

        StepVerifier.create(integerFlux)
                .expectNext(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .expectComplete()
                .verify();
    }

    @Test
    public void CreateFluxRange() {
        Flux<Integer> integerFlux = Flux.range(0, 10);

        StepVerifier.create(integerFlux)
                .expectNext(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .expectComplete()
                .verify();
    }

    @Test
    public void CreateFluxInterval() {
        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(100))
                .take(10);

        StepVerifier.create(longFlux)
                .expectNext(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
                .expectComplete()
                .verify();
    }

    @Test
    public void mergeFluxes() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
                .delayElements(Duration.ofMillis(500));
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));
        Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);
        StepVerifier.create(mergedFlux)
                .expectNext("Garfield")
                .expectNext("Lasagna")
                .expectNext("Kojak")
                .expectNext("Lollipops")
                .expectNext("Barbossa")
                .expectNext("Apples")
                .verifyComplete();
    }

    @Test
    public void zipFluxes() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");
        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);
        StepVerifier.create(zippedFlux)
                .expectNextMatches(p -> p.getT1()
                        .equals("Garfield") && p.getT2()
                        .equals("Lasagna"))
                .expectNextMatches(p -> p.getT1()
                        .equals("Kojak") && p.getT2()
                        .equals("Lollipops"))
                .expectNextMatches(p -> p.getT1()
                        .equals("Barbossa") && p.getT2()
                        .equals("Apples"))
                .verifyComplete();
    }

    @Test
    public void zipFluxesSimple() {
        Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");
        var zipped = Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);
        StepVerifier.create(zipped)
                .expectNext("Garfield eats Lasagna")
                .expectNext("Kojak eats Lollipops")
                .expectNext("Barbossa eats Apples")
                .verifyComplete();

    }

    @Test
    public void firstWithSignalFlux() {
        Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth")
                .delaySubscription(Duration.ofMillis(100));
        Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");
        Flux<String> firstFlux = Flux.firstWithSignal(slowFlux, fastFlux);
        StepVerifier.create(firstFlux)
                .expectNext("hare")
                .expectNext("cheetah")
                .expectNext("squirrel")
                .verifyComplete();
    }

    @Test
    public void skipAFew() {
        Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
                .skip(3);
        StepVerifier.create(countFlux)
                .expectNext("ninety nine", "one hundred")
                .verifyComplete();
    }

    @Test
    public void skipAFewSeconds() {
        Flux<String> countFlux = Flux.just("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(3));
        StepVerifier.create(countFlux)
                .expectNext("three", "four", "five", "six", "seven", "eight", "nine", "ten")
                .verifyComplete();
    }

    @Test
    void testMap() {
        Flux<Person> personFlux = Flux.just(new Person("John", "Doe"), new Person("Jane", "Smith"), new Person("Emily", "Johnson"));

        Flux<String> fullNameFlux = personFlux.map(person -> person.getFirstName() + " " + person.getLastName());

        StepVerifier.create(fullNameFlux)
                .expectNext("John Doe")
                .expectNext("Jane Smith")
                .expectNext("Emily Johnson")
                .verifyComplete();
    }

    static class Person {
        private final String firstName;
        private final String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @Test
    void testFlatMap() throws InterruptedException {

        Flux<Integer> integerFlux = Flux.fromStream(IntStream.range(0, 5)
                .boxed());

        Flux<Integer> flatMappedFlux = integerFlux.flatMap(i -> Mono.just(i)
                        .delayElement(Duration.ofSeconds(1)))
                .subscribeOn(Schedulers.parallel());
        flatMappedFlux.subscribe(System.out::println);

        Thread.sleep(5000);


    }

    @Test
    void testFlatMapSequential() throws InterruptedException {

        Flux<Integer> integerFlux = Flux.fromStream(IntStream.range(0, 5)
                .boxed());

        Flux<Integer> flatMappedFlux = integerFlux.flatMapSequential(i -> Mono.just(i)
                        .delayElement(Duration.ofSeconds(3)))
                .subscribeOn(Schedulers.parallel());
        flatMappedFlux.subscribe(System.out::println);

        Thread.sleep(3000);
    }

    @Test
    void testFlatMapToParallel() throws InterruptedException {

        Flux<Integer> integerFlux = Flux.fromStream(IntStream.range(0, 5)
                .boxed());

        Flux<Integer> flatMappedFlux = integerFlux.flatMap(i -> Mono.just(i)
                        .delayElement(Duration.ofSeconds(3)))
                .subscribeOn(Schedulers.parallel());
        flatMappedFlux.subscribe(i -> System.out.println("Received: " + i + " on thread " + Thread.currentThread()
                .getName()));

        Thread.sleep(3000);
    }

    @Test
    void testFlatMapToParallelOrdered() throws InterruptedException {

        System.out.println("Main Thread: " + Thread.currentThread()
                .getName());
        Flux<Integer> integerFlux = Flux.fromStream(IntStream.range(0, 10)
                .boxed());

        Flux<Integer> flatMappedFlux = integerFlux.flatMap(i -> Mono.just(i)
                        .delayElement(Duration.ofSeconds(3)))
                .subscribeOn(Schedulers.boundedElastic());
        flatMappedFlux.subscribe(i -> System.out.println("Received: " + i + " on thread " + Thread.currentThread()
                .getName()));

        Thread.sleep(8000);
    }

    @Test
    public void testFluxBuffering() {
        Flux<String> fruitFlux = Flux.just(
                "apple", "orange", "banana", "kiwi", "strawberry");
        Flux<List<String>> bufferedFlux = fruitFlux.buffer(3);
        StepVerifier
                .create(bufferedFlux)
                .expectNext(Arrays.asList("apple", "orange", "banana"))
                .expectNext(Arrays.asList("kiwi", "strawberry"))
                .verifyComplete();
    }

    @Test
    public void bufferAndFlatMap() throws Exception {
        Flux.just(
                        "apple", "orange", "banana", "kiwi", "strawberry")
                .buffer(3)
                // Process each buffer in parallel and flatten the results
                // back into a single Flux
                .flatMap(x ->
                                Flux.fromIterable(x)
                                        .map(String::toUpperCase)
                                        .subscribeOn(Schedulers.parallel())
//                                .log()
                                        .doOnNext(s -> System.out.println("Received: " + s + " on thread " + Thread.currentThread()))

                )
                .subscribe();

    }

    @Test
    public void collectList() {
        Flux<String> fruitFlux = Flux.just(
                "apple", "orange", "banana", "kiwi", "strawberry");
        Mono<List<String>> fruitListMono = fruitFlux.collectList();
        StepVerifier
                .create(fruitListMono)
                .expectNext(Arrays.asList(
                        "apple", "orange", "banana", "kiwi", "strawberry"))
                .verifyComplete();
    }

    @Test
    public void collectMap() {
        Flux<String> animalFlux = Flux.just(
                "aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Map<Character, String>> animalMapMono =
                animalFlux.collectMap(a -> a.charAt(0));


        // Verify that the resulting map contains the expected key-value pairs
        StepVerifier
                .create(animalMapMono)
                .expectNextMatches(map -> map.size() == 3 &&
                        map.get('a')
                                .equals("aardvark") &&
                        map.get('e')
                                .equals("eagle") &&
                        map.get('k')
                                .equals("kangaroo"))
                .verifyComplete();
    }

    @Test
    public void all() {
        Flux<String> animalFlux = Flux.just(
                "aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Boolean> hasAMono = animalFlux.all(a -> a.contains("a"));
        StepVerifier.create(hasAMono)
                .expectNext(true)
                .verifyComplete();
        Mono<Boolean> hasKMono = animalFlux.all(a -> a.contains("k"));
        StepVerifier.create(hasKMono)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void any() {
        Flux<String> animalFlux = Flux.just(
                "aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Boolean> hasAMono = animalFlux.any(a -> a.contains("a"));
        StepVerifier.create(hasAMono)
                .expectNext(true)
                .verifyComplete();
        Mono<Boolean> hasZMono = animalFlux.any(a -> a.contains("z"));
        StepVerifier.create(hasZMono)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void doOnNextDemo() {
        Flux<String> animalFlux = Flux.just(
                "aardvark", "elephant", "koala", "eagle", "kangaroo");
        animalFlux
                .doOnNext(a -> System.out.println("Processing: " + a))
                .map(String::toUpperCase)
                .subscribe(a -> System.out.println("Received: " + a));
    }

    @Test
    public void doOnErrorDemo() {
        Flux<String> animalFlux = Flux.just(
                        "aardvark", "elephant", "koala", "eagle", "kangaroo")
                .map(a -> {
                    if (a.startsWith("k")) {
                        throw new RuntimeException("Error processing: " + a);
                    }
                    return a.toUpperCase();
                })
                .doOnError(e -> System.err.println("Caught error: " + e.getMessage()));
        animalFlux.subscribe(
                a -> System.out.println("Received: " + a),
                e -> System.err.println("Subscription error: " + e.getMessage())
        );
    }

    @Test
    public void logReactiveStream() {
        Flux<String> animalFlux = Flux.just(
                        "aardvark", "elephant", "koala", "eagle", "kangaroo")
                .log();
        StepVerifier.create(animalFlux)
                .expectNext("aardvark", "elephant", "koala", "eagle", "kangaroo")
                .verifyComplete();
    }
}