package com.kiseokapi.demo.events;

import com.kiseokapi.demo.common.ErrorResource;
import com.kiseokapi.demo.index.IndexController;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
            return ResponseEntity.badRequest().body(new ErrorResource(errors));
            /** 에러가 생겼을 때 루트로 돌아가도록 해줄려면 링크를 담아야 한다 그래서 errorResource추가해주기*/
        }
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        // 링크를 담은 리소스를 만들어서 본문 응답으로 리턴해준다.
        EventResource eventResource = new EventResource(newEvent); // 이 때 생성자로 셀프링크는 생성하고
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile")); //createEvent니까 -create로
        eventResource.add(linkTo(EventController.class).slash(newEvent.getId()).withRel("update-event"));//self와 url은 같지만 put등으로
        return ResponseEntity.created(createdUri).body(eventResource); //created는 uri가 필요하고 위에서 그걸 만든 것
    }
    //spring.jackson.deserialization.fail-on-unknown-properties=true

    @GetMapping         /**  페이지도 결국 링크를 달아주기위해 리소스로 처리해야하는데 그 때 유용한게 PagedResourcesAssembler*/
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        /**  테스트코드에서 페이징 파라미터를 어떻게 받는지 볷브*/
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = assembler.toModel(page,e->new EventResource(e));
        /** 매우 중요 !! 페이지에 ! 대한 링크 다음페이지 등등은 존재하지만 각각의 이벤트마다는 링크가 없다
         * 그래서 각각의 이벤트들을 이벤트 리로스로 변경해주기위해 e->new EventResource(e) 추가해준다 파라미터로*/
        // resource로 변경을 해주면
        //페이지와 관련된 기본적인 링크를 알아서 제공해준다
        //self prev last page.. 등을 기본으로 제공
        entityModels.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> byId = eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = byId.orElseThrow();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {
        Event event = eventRepository.findById(id).orElseThrow();
        if (event == null) {
            /**  1. 비어있는 경우*/
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResource(errors));
        }
        modelMapper.map(eventDto, event);
        Event newEvent = eventRepository.save(event);
        // 원래는 service단에서 트랜잭션으로 데이터변경 후
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }
}
