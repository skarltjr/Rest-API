package com.kiseokapi.demo.events;


import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("REST API")
                .description("스프링 rest api")
                .build();
        assertThat(event).isNotNull();
    }
    @Test
    public void javaBean() {
        Event event = new Event();
        event.setName("이름");
        event.setDescription("소개");
        assertThat(event).isNotNull();
        assertThat(event.getName()).isEqualTo("이름");
        assertThat(event.getDescription()).isEqualTo("소개");
    }


}