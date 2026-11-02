package com.example.material.web;
import com.example.material.model.*; import com.example.material.service.*; import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile; import java.io.IOException; import java.util.*;
@RestController @RequestMapping("/api") public class ApiController {
 private final TemplateService templateService; private final TaskService taskService; public ApiController(TemplateService t,TaskService s){templateService=t;taskService=s;}
 private String user(HttpServletRequest r){return Optional.ofNullable(r.getHeader("X-Demo-User")).filter(x->!x.isBlank()).orElse("demo-user");}
 @GetMapping("/templates") public List<Template> templates(){return templateService.all();} @GetMapping("/templates/{id}") public ResponseEntity<Template> template(@PathVariable String id){Template t=templateService.get(id);return t==null?ResponseEntity.notFound().build():ResponseEntity.ok(t);}
 @PostMapping(value="/tasks",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<?> create(@RequestParam String templateId,@RequestPart("files") List<MultipartFile> files,HttpServletRequest req) throws IOException {try{return ResponseEntity.accepted().body(taskService.create(user(req),templateId,files));}catch(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
 @GetMapping("/tasks") public List<Task> history(HttpServletRequest r){return taskService.history(user(r));} @GetMapping("/tasks/{id}") public ResponseEntity<?> task(@PathVariable String id,HttpServletRequest r){Task t=taskService.get(id,user(r));return t==null?ResponseEntity.notFound().build():ResponseEntity.ok(t);}
}
