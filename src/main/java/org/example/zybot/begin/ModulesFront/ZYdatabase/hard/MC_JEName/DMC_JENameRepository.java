package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_JEName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DMC_JENameRepository extends JpaRepository<DMC_JEName, Integer> {

    Optional<DMC_JEName> findByBidAndName(String bid, String name);

    DMC_JEName findByBidAndIpAndPost(String bid, String ip, int post);

    List<DMC_JEName> findByBid(String bid);
}