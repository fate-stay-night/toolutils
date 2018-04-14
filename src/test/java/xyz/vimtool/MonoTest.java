package xyz.vimtool;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 
 *
 * @author zhangzheng
 * @version 1.0
 * @since jdk1.8
 * @date 2018-4-12
 */
public class MonoTest {

    @Test
    public void testMonoBasic(){
        Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
    }
}
