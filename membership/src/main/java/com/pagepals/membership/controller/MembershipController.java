package com.pagepals.membership.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.membership.dto.MembershipRequestDTO;
import com.pagepals.membership.dto.ParticipantDTO;
import com.pagepals.membership.service.MembershipService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    public final MembershipService membershipService;

    @PostMapping("/inscription")
    public ResponseEntity<Void> inscrire(@RequestBody MembershipRequestDTO dto) {
        membershipService.inscrire(dto.getUserId(), dto.getCircleId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/desinscrire")
    public ResponseEntity<Void> desinscrire(@RequestBody MembershipRequestDTO dto) {
        membershipService.desinscrire(dto.getUserId(), dto.getCircleId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> estInscrit(@RequestParam Long userId, @RequestParam Long circleId) {
        boolean inscrit = membershipService.estInscrit(userId, circleId);
        return ResponseEntity.ok(inscrit);
    }

    @GetMapping("/circle/{circleId}/participants")
    public ResponseEntity<List<ParticipantDTO>> getParticipantsWithPseudo(@PathVariable Long circleId) {
        return ResponseEntity.ok(membershipService.getParticipantsWithPseudo(circleId));
    }
}
