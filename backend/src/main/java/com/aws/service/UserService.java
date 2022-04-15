package com.aws.service;

import com.aws.model.User;
import com.aws.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getCurrentUser(){
        String sub = ((Jwt)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("sub");

        return userRepository.findBySub(sub).orElseThrow(()->new IllegalArgumentException("Cannot find user with sub"+sub));
    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikedVideos(videoId);
        userRepository.save(currentUser);

    }
    public boolean ifLikedVideo(String videoId){
        return getCurrentUser().getLikedVideos().stream().anyMatch(likedvideo->likedvideo.equals(videoId));
    }

    public boolean ifDisLikedVideo(String videoId){
        return getCurrentUser().getDislikedVideos().stream().anyMatch(likedvideo->likedvideo.equals(videoId));
    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);
        userRepository.save(currentUser);

    }

    public void removeFromDislikeCount(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDisLikedVideos(videoId);
        userRepository.save(currentUser);

    }

    public void addToDisLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDisLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addVideoToHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoId);
        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId) {
        //Retrieve the Current User
        // and add the UserId to the Users Set
        //Retrieve the targetUser and add current user to Subscriber List
        User currentUser = getCurrentUser();
        currentUser.addToSubscribersToUsers(userId);
        User user = getUserById(userId);
        user.addToSubscribers(currentUser.getId());
        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public void unSubscribeUser(String userId) {

        User currentUser = getCurrentUser();
        currentUser.removeFromSubscribersToUsers(userId);
        User user = getUserById(userId);
        user.removeFromSubscribers(currentUser.getId());
        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public Set<String> userHistory(String userId) {
        User user = getUserById(userId);
        return user.getVideoHistory();
    }

    private User getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("Cannot find User"+ userId));
        return user;
    }
}


