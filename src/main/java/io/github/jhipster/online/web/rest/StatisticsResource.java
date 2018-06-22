package io.github.jhipster.online.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.online.domain.EntityStats;
import io.github.jhipster.online.domain.SubGenEvent;
import io.github.jhipster.online.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;

@RestController
@RequestMapping("/api/s")
public class StatisticsResource {

    private final Logger log = LoggerFactory.getLogger(StatisticsResource.class);

    private final StatisticsService statisticsService;

    private final YoRCService yoRCService;

    private final JdlService jdlService;

    private final UserService userService;

    private final GeneratorIdentityService generatorIdentityService;

    public StatisticsResource(StatisticsService statisticsService, YoRCService yoRCService, JdlService jdlService, UserService userService, GeneratorIdentityService generatorIdentityService) {
        this.statisticsService = statisticsService;
        this.yoRCService = yoRCService;
        this.jdlService = jdlService;
        this.userService = userService;
        this.generatorIdentityService = generatorIdentityService;
    }

    @GetMapping("/count-yorc/{date}")
    @Timed
    public long getYoRcCount(@PathVariable Instant date) {
        return yoRCService.countAllByCreationDate(date);
    }

    @GetMapping("/count-yorc")
    @Timed
    public long getYoRcCount() {
        return yoRCService.countAll();
    }

    @GetMapping("/count-jdl")
    @Timed
    public long getJdlCount() {
        return jdlService.countAll();
    }

    @GetMapping("/count-user")
    @Timed
    public long getUserCount() { return userService.countAll(); }

    @PostMapping("/entry")
    @Timed
    public ResponseEntity addYoRc(HttpServletRequest req, @RequestBody String entry) {
        try {
            statisticsService.addEntry(entry, req.getRemoteHost());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/event/{generatorId}")
    @Timed
    public ResponseEntity addSubGenEvent(@RequestBody SubGenEvent event, @PathVariable String generatorId) {
        statisticsService.addSubGenEvent(event, generatorId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/entity/{generatorId}")
    @Timed
    public ResponseEntity addEntityStats(@RequestBody EntityStats entity, @PathVariable String generatorId) {
        statisticsService.addEntityStats(entity, generatorId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/link")
    @Timed
    public void linkGeneratorToCurrentUser(String generatorId) {
        log.info("Linking current user to generator {}", generatorId);
        generatorIdentityService.bindCurrentUserToGenerator(generatorId);
    }
}
