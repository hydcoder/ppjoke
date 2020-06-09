package com.hyd_coder.libnetwork.cache;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/9 16:57
 * description : 操作Cache表的dao接口，具体实现由Room编译后生成
 */
@Dao
public interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Cache cache);

    /**
     * 注意，冒号后面必须紧跟参数名，中间不能有空格。大于小于号和冒号中间是有空格的。
     * select *from cache where【表中列名】 =:【参数名】------>等于
     * where 【表中列名】 < :【参数名】 小于
     * where 【表中列名】 between :【参数名1】 and :【参数2】------->这个区间
     * where 【表中列名】like :参数名----->模糊查询
     * where 【表中列名】 in (:【参数名集合】)---->查询符合集合内指定字段值的记录
     *
     * @param key 缓存的key
     * @return 缓存的数据
     */
    // 如果是一对多,这fan返回List<Cache>
    @Query("select * from cache where `key`=:key")
    Cache getCache(String key);

    // 只能传递对象, 删除是根据Cache中的主键来比对的
    int delete(Cache cache);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Cache cache);
}
