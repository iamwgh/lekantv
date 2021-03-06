package com.pxf.fftv.plus.player;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.common.InternalFileSaveUtil;
import com.pxf.fftv.plus.contract.history.VideoHistory;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.LinkedList;

class TBSVideoPlayer implements IVideoPlayer {

    private volatile static TBSVideoPlayer mInstance;

    private TBSVideoPlayer() {
    }

    static TBSVideoPlayer getInstance() {
        if (mInstance == null) {
            synchronized (TBSVideoPlayer.class) {
                if (mInstance == null) {
                    mInstance = new TBSVideoPlayer();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void play(Activity activity, String url, String title, String subTitle, int currentPart, String picUrl, int lastPosition) {
        // 腾讯X5在播放前保存历史
        // 保存历史
        VideoHistory videoHistory = new VideoHistory();

        videoHistory.setLastPosition(0);
        videoHistory.setTitle(title);
        videoHistory.setSubTitle(subTitle);
        videoHistory.setUrl(url);
        videoHistory.setDuration(1);
        videoHistory.setPicUrl(picUrl);

        LinkedList<VideoHistory> historyList = (LinkedList<VideoHistory>) InternalFileSaveUtil.getInstance(activity).get("video_history");
        if (historyList == null) {
            historyList = new LinkedList<>();
        }
        historyList.add(0, videoHistory);
        // 同名视频只添加去除之前的历史
        if (historyList.size() > 1) {
            for (int i = 1 ; i < historyList.size() ; i++) {
                if (historyList.get(i).getTitle().equals(title)) {
                    historyList.remove(i);
                    break;
                }
            }
        }
        if (historyList.size() > Const.VIDEO_HISTORY_NUM) {
            historyList.remove(historyList.size() - 1);
        }
        InternalFileSaveUtil.getInstance(activity).put("video_history", historyList);

        Bundle bundle = new Bundle();
        bundle.putInt("screenMode", 102);
        TbsVideo.openVideo(activity, url, bundle);
    }
}
