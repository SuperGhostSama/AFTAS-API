package com.example.aftas.service.impl;

import com.example.aftas.config.handlers.exception.ResourceNotFoundException;
import com.example.aftas.model.*;
import com.example.aftas.repository.HuntingRepository;
import com.example.aftas.service.HuntingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HuntingServiceImpl implements HuntingService {
    private final HuntingRepository huntingRepository;
    private final FishServiceImpl fishService;
    private final MemberServiceImpl memberService;
    private final CompetitionServiceImpl competitionService;
    private final RankingServiceImpl rankingService;

    public HuntingServiceImpl(HuntingRepository huntingRepository, FishServiceImpl fishService, MemberServiceImpl memberService, CompetitionServiceImpl competitionService, RankingServiceImpl rankingService) {
        this.huntingRepository = huntingRepository;
        this.fishService = fishService;
        this.memberService = memberService;
        this.competitionService = competitionService;
        this.rankingService = rankingService;
    }

    @Override
    public Hunting getHuntingById(Long id) {
        return huntingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hunting id " + id + " not found"));
    }

    @Override
    public Hunting addHuntingResult(Hunting hunting) {
        Long competitionId = hunting.getCompetition().getId();
        Long memberId = hunting.getMember().getId();
        Long fishId = hunting.getFish().getId();
        // check if competition exist
        Competition competition = competitionService.getCompetitionById(competitionId);
        // check if member exist
        Member member = memberService.getMemberById(memberId);
        // check if fish exist
        Fish fish = fishService.getFishById(fishId);
        // check if Member has already participated in this competition
        rankingService.getRankingsByMemberIdAndCompetitionId(competitionId, memberId);
        // check if fish has level
        if (fish.getLevel() == null) {
            throw new ResourceNotFoundException("Fish id " + fishId + " has no level");
        }
        // check weight of fish must be greater than average weight
        if (hunting.getFish().getWeight() < fish.getWeight()) {
            throw new ResourceNotFoundException("Weight of fish must be greater than average weight");
        }
        // check if fish has already been caught by this member in this competition if yes acquirement the number of fish caught
        Hunting existingHunting = huntingRepository.findByCompetitionIdAndMemberIdAndFishId(competitionId, memberId, fishId);


        Ranking ranking = rankingService.getRankingsByMemberIdAndCompetitionId(competitionId, memberId);
        ranking.setScore(ranking.getScore() + fish.getLevel().getPoint());
        rankingService.updateRanking(ranking, competitionId, memberId);

        if(existingHunting != null) {
            existingHunting.setNumberOfFish(existingHunting.getNumberOfFish() + 1);
            return huntingRepository.save(existingHunting);
        } else {
            return huntingRepository.save(hunting);
        }
    }

    @Override
    public List<Hunting> getHuntingsByCompetition(Long competitionId) {
        return huntingRepository.findByCompetitionId(competitionId);
    }

    @Override
    public List<Hunting> getHuntingsByCompetitionAndMember(Long competitionId, Long memberId) {
        return huntingRepository.findByCompetitionIdAndMemberId(competitionId, memberId);
    }

    @Override
    public Hunting updateHunting(Hunting hunting, Long id) {
        Hunting existingHunting = getHuntingById(id);
        existingHunting.setFish(hunting.getFish());
        existingHunting.setMember(hunting.getMember());
        existingHunting.setCompetition(hunting.getCompetition());
        existingHunting.setNumberOfFish(hunting.getNumberOfFish());
        return huntingRepository.save(existingHunting);
    }

    @Override
    public void deleteHunting(Long id) {
        getHuntingById(id);
        huntingRepository.deleteById(id);
    }
}
