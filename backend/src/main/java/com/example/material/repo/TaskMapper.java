package com.example.material.repo;
import com.example.material.model.Task;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
@Mapper public interface TaskMapper {
 @Insert("INSERT INTO tasks(id,user_id,template_id,status,stage,created_at,updated_at) VALUES(#{id},#{userId},#{templateId},#{status},#{stage},#{createdAt},#{updatedAt})") void insert(Task t);
 @Select("SELECT id,user_id,template_id,status,stage,error_message,result_url,created_at,updated_at FROM tasks WHERE id=#{id} AND user_id=#{userId}") Task find(String id,String userId);
 @Select("SELECT id,user_id,template_id,status,stage,error_message,result_url,created_at,updated_at FROM tasks WHERE user_id=#{userId} ORDER BY created_at DESC") List<Task> findByUser(String userId);
 @Update("UPDATE tasks SET status=#{status},stage=#{stage},error_message=#{errorMessage},result_url=#{resultUrl},updated_at=#{updatedAt} WHERE id=#{id}") void update(Task t);
}
