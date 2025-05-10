package org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DSleepMornRecordsRepository extends JpaRepository<DSleepMornRecords, Long> {

    //通过人员编号、群聊/频道号、房间号综合查询对应人员信息
    @Query("SELECT a from DSleepMornRecords a where a.Uid= :Uid AND a.Bid = :Bid")
    DSleepMornRecords findByUidAndBid(@Param("Uid") String Uid, @Param("Bid") String Bid);

    @Query("SELECT a FROM DSleepMornRecords a WHERE a.Uid = :Uid")
    DSleepMornRecords findByUid(String Uid);
}