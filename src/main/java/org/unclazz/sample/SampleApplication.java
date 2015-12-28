package org.unclazz.sample;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;

/**
 * このサンプル・アプリケーションのエントリーポイントとなるオブジェクト.
 * <p>{@link EnableAutoConfiguration}や{@link ComponentScan}といったアノテーションにより
 * Spring Bootアプリケーションの起動方法を規定する。</p>
 */
@EnableAutoConfiguration
@ComponentScan
public class SampleApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleApplication.class, args);
    }
}