package com.example.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class UnCheckedTest {

    @Test
    @DisplayName("UnChecked 예외를 처리한다.")
    void unChecked_exception_catch_test() {
        Service service = new Service();
        service.callCatchException();
    }

    @Test
    @DisplayName("UnChecked 예외를 던진다.")
    void unChecked_exception_throw_test() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrowException()).isInstanceOf(MyUnCheckedException.class);
    }


    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUnCheckedException extends RuntimeException {
        public MyUnCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatchException() {
            try {
                repository.call();
            } catch (MyUnCheckedException e) {
                //예외 처리 로직
                log.info("예외처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
         */
        public void callThrowException() {
            repository.call();
        }

    }

    static class Repository {
        public void call() {
            throw new MyUnCheckedException("ex");
        }
    }
}
