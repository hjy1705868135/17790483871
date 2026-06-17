/**
 * 无人机数据访问接口（Repository层）
 * 定义无人机表的CRUD操作方法
 * MyBatis通过XML配置文件实现这些方法的具体SQL逻辑
 */
package com.md.basePlatform.repository;

// 导入无人机实体类
import com.md.basePlatform.domain.Drone;
// 导入MyBatis的Mapper注解
import org.apache.ibatis.annotations.Mapper;
// 导入MyBatis的参数注解，用于指定参数名称
import org.apache.ibatis.annotations.Param;

// 导入Java集合框架的List接口
import java.util.List;

/**
 * 无人机Mapper接口（数据操作层接口）
 * 提供无人机数据的持久化操作
 * 使用@Mapper注解标记，MyBatis会自动扫描并创建实现类
 */
@Mapper
public interface DroneMapper {

    /**
     * 查询所有无人机记录
     * 
     * @return 返回包含所有无人机的List集合
     */
    List<Drone> findAll();

    /**
     * 根据ID查询单个无人机
     * 
     * @param id 无人机的唯一标识ID
     * @return 返回匹配的无人机对象，若不存在则返回null
     */
    Drone findById(@Param("id") Long id);

    /**
     * 根据名称模糊查询无人机
     * 
     * @param name 名称关键词（已包含%通配符）
     * @return 返回匹配条件的无人机List集合
     */
    List<Drone> findByName(@Param("name") String name);

    /**
     * 根据型号模糊查询无人机
     * 
     * @param model 型号关键词（已包含%通配符）
     * @return 返回匹配条件的无人机List集合
     */
    List<Drone> findByModel(@Param("model") String model);

    /**
     * 插入新的无人机记录
     * 
     * @param drone 要插入的无人机对象
     * @return 返回受影响的行数
     */
    int insert(Drone drone);

    /**
     * 更新已有的无人机记录
     * 
     * @param drone 包含更新数据的无人机对象（必须包含ID）
     * @return 返回受影响的行数
     */
    int update(Drone drone);

    /**
     * 根据ID删除无人机记录
     * 
     * @param id 要删除的无人机ID
     * @return 返回受影响的行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计无人机总数
     * 
     * @return 返回无人机表中的总记录数
     */
    int count();
}
