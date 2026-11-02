package com.example.material.repo;
import com.example.material.model.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper public interface TemplateMapper {
 @Select("SELECT id,title,category,cover_url,description,required_image_count,accepted_mime_types,max_file_size_bytes FROM templates ORDER BY id") List<Template> findAll();
 @Select("SELECT id,title,category,cover_url,description,required_image_count,accepted_mime_types,max_file_size_bytes FROM templates WHERE id=#{id}") Template findById(String id);
}
