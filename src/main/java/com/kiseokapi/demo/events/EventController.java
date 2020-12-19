package com.kiseokapi.demo.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @InitBinder("eventDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }


    /** hateos의 Location URI만들기 그럼
     만약에 PostMapping("/~/")이라면 URL이 컨트롤러자체에 있는게 아니니까 linkTo안에 methodOn까지 필요
     Location:"http://localhost/api/events/10 로 로케이션 정보를 담은 URI가 만들어진다
     requestmapping에 붙어있는걸 linkto로받고 slash + 이벤트의 Id 그대로
     "*/
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        // 링크를 담은 리소스를 만들어서 본문 응답으로 리턴해준다.
        EventResource eventResource = new EventResource(newEvent); // 이 때 생성자로 셀프링크는 생성하고
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkTo(EventController.class).slash(newEvent.getId()).withRel("update-event"));//self와 url은 같지만 put등으로
        return ResponseEntity.created(createdUri).body(eventResource); //created는 uri가 필요하고 위에서 그걸 만든 것
    }
    //spring.jackson.deserialization.fail-on-unknown-properties=true

}
