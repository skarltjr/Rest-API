package com.kiseokapi.demo.index;

import com.kiseokapi.demo.events.EventController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {
    /**  메인 화면 , 즉 첫화면루트에 대해
     *  그래서 만약 에러가 생기면 각각 리소스에 루트로 돌아갈 ㅅ 있도록 링크도 다 추가해주기 */
    @GetMapping("/api")
    public RepresentationModel index() {  //링크정보만 담을거라 RepresentationModel
        var index = new RepresentationModel<>();
        index.add(linkTo(EventController.class).withRel("events"));
        return index;
    }
}
