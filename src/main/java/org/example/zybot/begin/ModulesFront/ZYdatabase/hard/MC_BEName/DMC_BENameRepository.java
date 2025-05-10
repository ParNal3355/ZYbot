package org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_BEName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DMC_BENameRepository extends JpaRepository<DMC_BEName, Integer> {

    Optional<DMC_BEName> findByBidAndName(String bid, String name);

    DMC_BEName findByBidAndIpAndPost(String bid, String ip, int post);

    List<DMC_BEName> findByBid(String bid);
}