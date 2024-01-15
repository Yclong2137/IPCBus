package com.ycl.sdk_base.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoViewAngleData implements Parcelable {
    public int retCode;
    /**
     * 远控ID 取值范围0～255
     */
    public int id;
    /**
     * 远程监控AVM视图切换状态
     * 0x00:VideoSuperVisionAVMView_CLOSE
     * 0x01:VideoSuperVisionAVMView_DVR
     * 0x02:VideoSuperVisionAVMView_Rear
     * 0x03:VideoSuperVisionAVMView_Left
     * 0x04:VideoSuperVisionAVMView_Right
     * 0x05:VideoSuperVisionAVMView_Interior
     */
    public int videoSupervisionAVMView;

    /**
     * 远程监控AVM视图切换响应失败原因
     * 0x00:VI_VideoSuperVIsionAVMVIewFRes_enum_Success
     * 0x01:VI_VideoSuperVIsionAVMVIewFRes_enum_Reason1
     * 0x02:VI_VideoSuperVIsionAVMVIewFRes_enum_Reason2
     * 0x03:VI_VideoSuperVIsionAVMVIewFRes_enum_Reason3
     * 0x04:VI_VideoSuperVIsionAVMVIewFRes_enum_Reason4
     */
    public int videoSupervisionAVMViewFRes;
    /**
     * 远程监控AVM视图切换响应成功失败
     * 0x00:VI_VideoSuperVIsionAVMVIewRes_enum_Failure
     * 0x01:VI_VideoSuperVIsionAVMVIewRes_enum_Success
     */
    public int videoSupervisionAVMViewRes;

    public VideoViewAngleData() {
    }

    protected VideoViewAngleData(Parcel in) {
        retCode = in.readInt();
        id = in.readInt();
        videoSupervisionAVMView = in.readInt();
        videoSupervisionAVMViewFRes = in.readInt();
        videoSupervisionAVMViewRes = in.readInt();
    }

    public void readFromParcel(Parcel in) {
        retCode = in.readInt();
        id = in.readInt();
        videoSupervisionAVMView = in.readInt();
        videoSupervisionAVMViewFRes = in.readInt();
        videoSupervisionAVMViewRes = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(retCode);
        dest.writeInt(id);
        dest.writeInt(videoSupervisionAVMView);
        dest.writeInt(videoSupervisionAVMViewFRes);
        dest.writeInt(videoSupervisionAVMViewRes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoViewAngleData> CREATOR = new Creator<VideoViewAngleData>() {
        @Override
        public VideoViewAngleData createFromParcel(Parcel in) {
            return new VideoViewAngleData(in);
        }

        @Override
        public VideoViewAngleData[] newArray(int size) {
            return new VideoViewAngleData[size];
        }
    };

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVideoSupervisionAVMView() {
        return videoSupervisionAVMView;
    }

    public void setVideoSupervisionAVMView(int videoSupervisionAVMView) {
        this.videoSupervisionAVMView = videoSupervisionAVMView;
    }

    public int getVideoSupervisionAVMViewFRes() {
        return videoSupervisionAVMViewFRes;
    }

    public void setVideoSupervisionAVMViewFRes(int videoSupervisionAVMViewFRes) {
        this.videoSupervisionAVMViewFRes = videoSupervisionAVMViewFRes;
    }

    public int getVideoSupervisionAVMViewRes() {
        return videoSupervisionAVMViewRes;
    }

    public void setVideoSupervisionAVMViewRes(int videoSupervisionAVMViewRes) {
        this.videoSupervisionAVMViewRes = videoSupervisionAVMViewRes;
    }

    @Override
    public String toString() {
        return "VideoViewAngleData{" +
                "retCode=" + retCode +
                ", id=" + id +
                ", videoSupervisionAVMView=" + videoSupervisionAVMView +
                ", videoSupervisionAVMViewFRes=" + videoSupervisionAVMViewFRes +
                ", videoSupervisionAVMViewRes=" + videoSupervisionAVMViewRes +
                '}';
    }
}

