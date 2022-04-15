package com.aws.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Document(value="Video")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
        private String id;
        private String title;
        private String description;
        private String userId;
        private AtomicInteger likes = new AtomicInteger(0);
        private AtomicInteger disLikes = new AtomicInteger(0);
        private Set<String> tags;
        private String videoUrl;
        private VideoStatus videoStatus;
        private AtomicInteger viewCount = new AtomicInteger(0);
        private String thumbnailUrl;
        private List<Comment> comments = new CopyOnWriteArrayList<>();

        public int likeCount() {
                return likes.get();
        }

        public int disLikeCount() {
                return disLikes.get();
        }

        public void incrementViewCount() {
                viewCount.incrementAndGet();
        }

        public void incrementLikes() {
                likes.incrementAndGet();
        }

        public void decrementLikes() {
                likes.decrementAndGet();
        }

        public void incrementDisLikes() {
                disLikes.incrementAndGet();
        }

        public void decrementDisLikes() {
                disLikes.decrementAndGet();
        }

        public void addComment(Comment comment) {
                comments.add(comment);
        }
}
