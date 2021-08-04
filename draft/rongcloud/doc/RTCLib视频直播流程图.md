### 集成RTCLib视频直播流程图

#### 主播端函数调用流程
```plantuml
@startuml
skinparam defaultFontName 微软雅黑
skinparam defaultFontSize 10
skinparam monochrome true
skinparam shadowing false

start
:init RongIMClient with appKey; 
if ("RongIMClient connect with token") then (success)
    :set RCRTCConfig options;
    :RCRTCEngine init with RCRTCConfig;
    :set RCRTCVideoStreamConfig options;
    :RCRTCEngine VideoStream set config with RCRTCVideoStreamConfig;
    :create RCRTCVideoView;
    :RCRTCEngine VideoStream setVideoView with RCRTCVideoView;
    :local Framelayout addView with RCRTCVideoView;
    :RCRTCEngine VideoStream open camera;
    if ("RCRTCEngine join room") then (success)
        :RCRTCRoom registerRoomListener;
        if ("RCRTCRoom publishDefaultStreams") then (success)
            :generate live stream liveUrl;
        else (failed)
        endif
        :RCRTCRoom subscribeAVStream;
    else (failed)
    endif
endif
stop
@enduml
```

#### 观众端函数调用流程
```plantuml
@startuml
skinparam defaultFontName 微软雅黑
skinparam defaultFontSize 10
skinparam monochrome true
skinparam shadowing false

start
:init RongIMClient with appKey; 
if ("RongIMClient connect with token") then (success)
    :set RCRTCConfig options;
    :RCRTCEngine init with RCRTCConfig;
    if ("RCRTCEngine subscribe live stream with 主播liveurl") then (success)
        :onVideoStreamReceived: handle 主播端video stream;
        :onAudioStreamReceived: handle 主播端audio stream;
    else (failed)
    endif
else (failed)
endif
stop
@enduml
```

