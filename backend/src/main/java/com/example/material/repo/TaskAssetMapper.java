package com.example.material.repo;
import com.example.material.model.TaskAsset;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper public interface TaskAssetMapper {
 @Insert("INSERT INTO task_assets(task_id,file_url,original_name,mime_type) VALUES(#{taskId},#{fileUrl},#{originalName},#{mimeType})") void insert(TaskAsset asset);
 @Select("SELECT id,task_id,file_url,original_name,mime_type FROM task_assets WHERE task_id=#{taskId}") List<TaskAsset> findByTask(String taskId);
}
