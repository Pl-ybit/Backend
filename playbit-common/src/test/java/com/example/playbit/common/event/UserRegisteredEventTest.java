package com.example.playbit.common.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisteredEventTest {

    @Test
    void userId로_생성된다() {
        UserRegisteredEvent event = new UserRegisteredEvent(1L);
        assertThat(event.userId()).isEqualTo(1L);
    }

    @Test
    void record이므로_동일한_값은_동등하다() {
        UserRegisteredEvent a = new UserRegisteredEvent(1L);
        UserRegisteredEvent b = new UserRegisteredEvent(1L);
        assertThat(a).isEqualTo(b);
    }
}
