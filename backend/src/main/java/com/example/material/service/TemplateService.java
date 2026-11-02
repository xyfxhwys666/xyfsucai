package com.example.material.service;
import com.example.material.model.Template; import com.example.material.repo.TemplateMapper; import org.springframework.stereotype.Service; import java.util.List;
@Service public class TemplateService { private final TemplateMapper mapper; public TemplateService(TemplateMapper mapper){this.mapper=mapper;} public List<Template> all(){return mapper.findAll();} public Template get(String id){return mapper.findById(id);} }
