package org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DSleepMornStatisticsGroupRepository extends JpaRepository<DSleepMornStatisticsGroup, String> {

    DSleepMornStatisticsGroup findByBid(String bid);
}