package pwr.piisw.backend.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.piisw.backend.models.ChanThread;
import pwr.piisw.backend.services.ChanThreadService;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChanThreadResource {
  private final ChanThreadService chanThreadService;

  public ChanThreadResource(ChanThreadService chanThreadService) {
    this.chanThreadService = chanThreadService;
  }

  @PostMapping("threads")
  public ResponseEntity<ChanThread> saveThread(@RequestBody ChanThread chanThread) {
    return new ResponseEntity<>(chanThreadService.saveThread(chanThread), HttpStatus.OK);
  }

  @GetMapping("/threads/{id}")
  public ResponseEntity<ChanThread> getChanThread(@PathVariable("id") int id) {
    ChanThread chanThread = chanThreadService.getChanThread(id);
    return new ResponseEntity<>(chanThread, HttpStatus.OK);
  }

  @GetMapping("threads")
  public ResponseEntity<List<ChanThread>> getAllChanThread(@RequestParam(required = false) int limit, @RequestParam(required = false) int offset) {
      List<ChanThread> allChanThreads = chanThreadService.getAllChanThreads(limit, offset);
      return new ResponseEntity<>(allChanThreads, HttpStatus.OK);

  }
}
