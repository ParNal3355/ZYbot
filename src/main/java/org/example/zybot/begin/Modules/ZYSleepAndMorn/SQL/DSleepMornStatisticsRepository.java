package org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DSleepMornStatisticsRepository extends JpaRepository<DSleepMornStatistics, String> {

    @Query("SELECT a from DSleepMornStatistics a where a.uid= :Uid AND a.bid = :Bid")
    DSleepMornStatistics findByUidAndBid(@Param("Uid") String Uid, @Param("Bid") String Bid);

    //将所有fuc1、fuc2、fuc3设置为0
    @Modifying
    @Query("UPDATE DSleepMornStatistics s SET s.fuc1 = 0, s.fuc2 = 0,s.fuc3 = 0")
    @Transactional
    void resetFucColumns();
}