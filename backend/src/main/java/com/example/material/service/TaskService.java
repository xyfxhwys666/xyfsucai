package com.example.material.service;
import com.example.material.model.*; import com.example.material.repo.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.scheduling.annotation.Async; import org.springframework.stereotype.Service; import org.springframework.web.multipart.MultipartFile;
import java.io.IOException; import java.nio.file.*; import java.time.LocalDateTime; import java.util.*;
@Service public class TaskService {
 private final TemplateMapper templates; private final TaskMapper tasks; private final TaskAssetMapper assets; private final Path storage; private final TaskRunner runner;
 public TaskService(TemplateMapper t,TaskMapper ta,TaskAssetMapper a,@Value("${app.storage-dir}") String dir,TaskRunner runner){templates=t;tasks=ta;assets=a;storage=Paths.get(dir);this.runner=runner;}
 public List<Task> history(String user){return tasks.findByUser(user);} public Task get(String id,String user){return tasks.find(id,user);}
 public Task create(String user,String templateId,List<MultipartFile> files) throws IOException {
  Template t=templates.findById(templateId); if(t==null) throw new IllegalArgumentException("模板不存在"); if(files==null||files.size()!=t.requiredImageCount()) throw new IllegalArgumentException("需要上传 "+t.requiredImageCount()+" 张图片");
  Set<String> types=Set.of(t.acceptedMimeTypes().split(",")); Files.createDirectories(storage); String id=UUID.randomUUID().toString(); LocalDateTime now=LocalDateTime.now(); Task task=new Task(id,user,templateId,"QUEUED","ASSET_CHECK",null,null,now,now);
  for(MultipartFile file:files){ if(file.isEmpty()||file.getSize()>t.maxFileSizeBytes()||!types.contains(file.getContentType())) throw new IllegalArgumentException("素材类型或大小不符合模板要求"); }
  tasks.insert(task);
  for(MultipartFile file:files){ String name=id+"-"+UUID.randomUUID()+"-"+Objects.requireNonNullElse(file.getOriginalFilename(),"upload"); Path target=storage.resolve(name).normalize(); if(!target.startsWith(storage.toAbsolutePath().normalize())) throw new IllegalArgumentException("非法文件名"); file.transferTo(target); assets.insert(new TaskAsset(0,id,"/uploads/"+name,file.getOriginalFilename(),file.getContentType())); }
  runner.run(task); return task;
 }
}
