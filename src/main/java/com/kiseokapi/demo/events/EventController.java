package com.kiseokapi.demo.events;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event) {
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        /** hateos의 Location URI만들기 그럼
         만약에 PostMapping("/~/")이라면 URL이 컨트롤러자체에 있는게 아니니까 linkTo안에 methodOn까지 필요
        Location:"http://localhost/api/events/10 로 로케이션 정보를 담은 URI가 만들어진다
         requestmapping에 붙어있는걸 linkto로받고 slash + 이벤트의 Id 그대로
         "*/
        return ResponseEntity.created(createdUri).body(event); //created는 uri가 필요하고 위에서 그걸 만든 것
    }
}
