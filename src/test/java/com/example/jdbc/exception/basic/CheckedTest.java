package com.example.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedTest {

    @Test
    @DisplayName("Checked 예외를 처리한다.")
    void checked_exception_catch_test() {
        Service service = new Service();
        service.callCatchException();
    }

    @Test
    @DisplayName("Checked 예외를 던진다.")
    void checked_exception_throw_test() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrowException()).isInstanceOf(MyCheckedException.class);
    }


    /**
     * Exception을 상속받는 예외는 체크예외가 된다.
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Checked 예외는 예외를 잡아서 처리하거나,
     * 던지거나 둘중 하나를 필수로 선택해야 한다.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * Checked 예외를 잡아서 처리하는 코드
         */
        public void callCatchException() {
            try {
                repository.call();
            } catch (MyCheckedException e) { // 정확하게 MyCheckedException 만 잡고 싶다면 catch 에 MyCheckedException을 적어주어야 한다.
                //예외 처리 로직
                log.info("예외처리, message = {}", e.getMessage(), e);
            }
        }

        /**
         * Checked 예외를 던지는 코드
         */
        public void callThrowException() throws MyCheckedException {
            repository.call();
        }

    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
