package com.aws.service;

import com.aws.dto.CommentDto;
import com.aws.dto.UploadVideoResponse;
import com.aws.dto.VideoDto;
import com.aws.model.Comment;
import com.aws.model.Video;
import com.aws.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;
    public UploadVideoResponse uploadVideo(MultipartFile multipartFile){
        //upload file to AWS S3
        //save video data to Database
        String videoUrl = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);
        var savedVideo = videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(),savedVideo.getVideoUrl());

    }

    public VideoDto editVideo(VideoDto videoDto) {
        //Find video by videoId
       var savedvideo = getVideoById(videoDto.getId());
        //Map the video by videoId
        savedvideo.setTitle(videoDto.getTitle());
        savedvideo.setDescription(videoDto.getDescription());
        savedvideo.setTags(videoDto.getTags());
        savedvideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedvideo.setVideoStatus(videoDto.getVideoStatus());
        //save the video to database
        videoRepository.save(savedvideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        var savedVideo = getVideoById(videoId);
        String thumbnailUrl = s3Service.uploadFile(file);
        savedVideo.setThumbnailUrl(thumbnailUrl);
        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }

    public Video getVideoById(String videoId){
        return videoRepository.findById(videoId).orElseThrow(()-> new IllegalArgumentException("Cannot find Video by Id "+videoId));
    }

    public VideoDto getVideoDetails(String videoId){
        Video savedVideo = getVideoById(videoId);
        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(videoId);
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(savedVideo.getVideoUrl());
        videoDto.setThumbnailUrl(savedVideo.getThumbnailUrl());
        videoDto.setId(savedVideo.getId());
        videoDto.setTitle(savedVideo.getTitle());
        videoDto.setDescription(savedVideo.getDescription());
        videoDto.setTags(savedVideo.getTags());
        videoDto.setVideoStatus(savedVideo.getVideoStatus());
        videoDto.setLikeCount(savedVideo.getLikes().get());
        videoDto.setDislikeCount(savedVideo.getDisLikes().get());
        videoDto.setViewCount(savedVideo.getViewCount().get());

        return videoDto;
    }

    public void increaseVideoCount(Video savedVideo){
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {
        //Get video by Id

        Video videoById = getVideoById(videoId);
        // Increment the likes count
        // If user already like the video, then decrement like count
        //If User already disliked the video, then increment like count and decrement dislike count

        if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        }else if (userService.ifDisLikedVideo(videoId)){
            videoById.decrementDisLikes();
            userService.removeFromDislikeCount(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }else{
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }
        return mapToVideoDto(videoById);

    }

    private VideoDto mapToVideoDto(Video videoById) {
        videoRepository.save(videoById);
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDisLikes().get());
        return videoDto;
    }

    public VideoDto disLike(String videoId) {

        //Get video by Id

        Video videoById = getVideoById(videoId);
        // Increment the likes count
        // If user already like the video, then decrement like count
        //If User already disliked the video, then increment like count and decrement dislike count

        if(userService.ifDisLikedVideo(videoId)){
            videoById.decrementDisLikes();
            userService.removeFromDislikeCount(videoId);
        }else if (userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        }else{
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        }
        return mapToVideoDto(videoById);


    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(commentDto.getAuthorId());
        video.addComment(comment);
        videoRepository.save(video);

    }

    public List<CommentDto> getAllComments(String videoId) {
        System.out.println("The VideoId is: "+videoId);
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getComments();
        return  commentList.stream().map(this::mapToCommentDto).collect(Collectors.toList());

    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        return commentDto;
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream().map(this::mapToVideoDto).collect(Collectors.toList());
    }
}
