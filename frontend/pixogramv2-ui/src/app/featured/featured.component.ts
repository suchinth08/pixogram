import { Component, OnInit } from '@angular/core';
import {VideoService} from "../video.service";
import {VideoDto} from "../video-dto";

@Component({
  selector: 'app-featured',
  templateUrl: './featured.component.html',
  styleUrls: ['./featured.component.css']
})
export class FeaturedComponent implements OnInit {
  featuredVideos: Array<VideoDto> = [];
  constructor(private videoService:VideoService) {
    this.videoService.getAllVideos().subscribe(response=>{
      this.featuredVideos=response;
    })
  }

  ngOnInit(): void {
  }

}
