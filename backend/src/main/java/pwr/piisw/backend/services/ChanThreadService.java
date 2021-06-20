package pwr.piisw.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pwr.piisw.backend.helper.OffsetBasedPageRequest;
import pwr.piisw.backend.models.*;
import pwr.piisw.backend.repository.ChanThreadRepo;
import pwr.piisw.backend.repository.ImageRepo;

@Service
public class ChanThreadService {
  private final ChanThreadRepo chanThreadRepo;
  private final ImageRepo imageRepo;

  @Autowired
  public ChanThreadService(ChanThreadRepo chanThreadRepo, ImageRepo imageRepo) {
    this.chanThreadRepo = chanThreadRepo;
    this.imageRepo = imageRepo;
  }

  public ChanThread saveThread(ChanThread chanThread) {
    System.out.println("ImageResourceId: " + chanThread.getImageResourceId());
    Optional<Image> i = imageRepo.findById(chanThread.getImageResourceId());
    if (i.isPresent()) {
      chanThread.setImgUrl("/resources/" + chanThread.getImageResourceId());
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    Map<String, String> accounts = chanThread.getAccounts();
    accounts.put(chanThread.getNickname(), encoder.encode(chanThread.getPassword()));
    chanThread.setAccounts(accounts);
    chanThread.setDate(LocalDateTime.now());
    return chanThreadRepo.save(chanThread);
  }

  public ChanThread getChanThread(Integer chanThreadId) {
    return chanThreadRepo.findAllBythreadId(chanThreadId);
  }

  public List<ChanThread> getAllChanThreads(int limit, int offset, boolean random) {
    if (random) {
      Pageable pageable = PageRequest.of(0, limit);
      return chanThreadRepo.findRandomChanThreads(pageable).getContent();
    } else {
      Pageable pageable =
          new OffsetBasedPageRequest(limit, offset, "threadId", Sort.Direction.DESC);
      return chanThreadRepo.findAll(pageable).getContent();
    }
  }
}
