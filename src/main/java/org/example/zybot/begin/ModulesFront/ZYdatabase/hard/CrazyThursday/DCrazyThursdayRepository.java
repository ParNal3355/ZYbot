package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.CrazyThursday;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//CrazyThursday表的JpaRepository接口
@Repository
public interface DCrazyThursdayRepository extends JpaRepository<DCrazyThursday, Integer> {

    // 这里可以添加一些自定义的查询方法，如果需要的话
}