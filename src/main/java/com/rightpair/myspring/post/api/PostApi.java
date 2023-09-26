package com.rightpair.myspring.post.api;

import com.rightpair.myspring.common.auth.Principal;
import com.rightpair.myspring.post.dto.CreatePostDto;
import com.rightpair.myspring.post.dto.GetPostDto;
import com.rightpair.myspring.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public record PostApi(PostService postService) {

  @GetMapping("/{post_id}")
  public ResponseEntity<GetPostDto.Response> getPost(
      @PathVariable("post_id") Long postId
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(postService.getPostById(postId));
  }

  @GetMapping
  public ResponseEntity<List<GetPostDto.Response>> getPostList(
      @PageableDefault Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(postService.getPostList(pageable));
  }

  @PostMapping
  public ResponseEntity<CreatePostDto.Response> createPost(
      @Valid @RequestBody CreatePostDto.ControllerRequest request,
      @AuthenticationPrincipal Principal principal
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(postService.createPost(
            CreatePostDto.Request.from(principal.memberId(), request)
        ));
  }

//  public ResponseEntity<UpdatePostDto.Response> updatePost(UpdatePostDto.Request request) {
//
//  }
//
//  public ResponseEntity<DeletePostDto.Response> deletePost(DeletePostDto.Request request) {
//
//  }

}
