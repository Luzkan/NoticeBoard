import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
} from '@angular/core';
import {AbstractCleanable} from '../../../core/cleanable/abstract-cleanable.component';
import {Optional} from '../../../core/types/optional.model';
import {Thread} from '../../models/thread.model';
import {Post} from '../../models/post.model';
import {PostService} from '../../services/post.service';
import {Pageable} from '../../../core/api/pageable.model';
import {Observable} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {map, shareReplay} from 'rxjs/operators';
import {EntryFormData} from '../../models/form/base-form-data.model';
import {PostFormData} from '../../models/form/post-form-data.model';

@Component({
  selector: 'app-thread',
  templateUrl: './thread.component.html',
  styleUrls: ['./thread.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThreadComponent extends AbstractCleanable implements OnInit {
  private static readonly pageLimit = 4;
  private readonly threadSource: Observable<Thread>;
  private pageable = Pageable.forLimit(ThreadComponent.pageLimit);

  thread: Optional<Thread>;
  allPostsLoaded = false;
  posts: Post[] = [];

  constructor(private readonly route: ActivatedRoute,
              private readonly changeDetector: ChangeDetectorRef,
              private readonly postService: PostService) {
    super();
    this.threadSource = this.route.data.pipe(map((data) => data.threadId), shareReplay(1));
  }

  ngOnInit(): void {
    this.addSubscription(
        this.threadSource.subscribe((thread) => {
          this.resetForThread(thread);
        }),
        'loadThread',
    );
  }

  resetForThread(thread: Thread): void {
    this.allPostsLoaded = false;
    this.thread = thread;
    this.posts = [];
    this.loadFirstPage();
  }

  loadFirstPage(): void {
    this.pageable = Pageable.forLimit(ThreadComponent.pageLimit);
    this.loadNextPage();
  }

  loadNextPage(): void {
    const threadId = this.getThread().id;
    this.addSubscription(
        this.postService.getPosts(threadId, this.pageable).subscribe((additionalPosts) => {
          this.setAdditionalPosts(additionalPosts);
          this.updatePageable(additionalPosts, this.pageable);
          this.markForCheck();
        }),
        'PageLoad',
    );
  }

  private updatePageable(posts: Post[], pageable: Pageable): void {
    const loaded = posts.length;
    pageable.shiftOffset(loaded);
    this.allPostsLoaded = loaded !== pageable.limit;
  }

  private setAdditionalPosts(posts: Post[]): void {
    const current = this.posts;
    this.posts = current.concat(posts);
  }

  savePost(entry: EntryFormData): void {
    const threadId = this.getThread().id;
    const post: PostFormData = {...entry, threadId: threadId};
    this.addSubscription(
        this.postService.savePost(post).subscribe(() => {
          this.allPostsLoaded = false;
          this.markForCheck();
        }),
        'savePost',
    );
  }

  private getThread(): Thread {
    return this.safeGetter(this.thread, 'thread');
  }

  private markForCheck(): void {
    this.changeDetector.markForCheck();
  }
}
