package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Permission表的JpaRepository接口
public interface DPermissionRepository extends JpaRepository<DPermission, Long> {

    //查询bid与fun对应的哪一行
    @Query("SELECT p FROM DPermission p WHERE p.key = :key AND p.fun = :fun")
    Optional<DPermission> findByKeyAndFun(@Param("key") String key, @Param("fun") String fun);

    //删除bid与fun对应的哪一行
    @Modifying
    @Query("DELETE FROM DPermission WHERE key = :bid AND fun = :fun")
    @Transactional
    int deleteByBidAndFun(@Param("bid") String bid, @Param("fun") String fun);


}