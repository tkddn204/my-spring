package com.rightpair.myspring.member.api;

import com.rightpair.myspring.member.dto.GetMemberDto;
import com.rightpair.myspring.member.dto.JoinMemberDto;
import com.rightpair.myspring.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public record MemberApi(MemberService memberService) {

  @PostMapping("/join")
  public ResponseEntity<JoinMemberDto.Response> joinMember(
      @Valid @RequestBody JoinMemberDto.Request request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(memberService.joinMember(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetMemberDto.Response> getMember(
      @Valid @PathVariable Long id
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(memberService.getMemberById(id));
  }
}
