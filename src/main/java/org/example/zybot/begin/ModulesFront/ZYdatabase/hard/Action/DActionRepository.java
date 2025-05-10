package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//关于DAction的JpaRepository方法
public interface DActionRepository extends JpaRepository<DAction, String> {
    //检测游戏是否存在的逻辑：bid+sid
    @Query("SELECT d FROM DAction d WHERE d.id.id = :id AND d.id.room = :room")
    Optional<DAction> findByIdAndRoom(String id, String room);

    //查询所有存在时间大于1h的记录
    @Query("SELECT a FROM DAction a WHERE a.CTime < :HourAgo")
    List<DAction> findActionsOlderThanOneHour(LocalDateTime HourAgo);


}
