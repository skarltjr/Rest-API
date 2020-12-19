package com.kiseokapi.demo.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
/** 중요한 것은 빈으로 등록하는게 아니다 . 매 번 새로운 대상에 대해서는 새로 만들어줘야한다. */
public class EventResource extends EntityModel<Event> {
    public EventResource(Event content, Link... links) {
        super(content, links);
        add(linkTo(EventController.class).slash(content.getId()).withSelfRel());
        //  add(new Link("http://~")) 링크를 만드는 방법에는 다양한 방법이 있지만 타입세이프한 방법을 사용
    }
    /*private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }*/
}
