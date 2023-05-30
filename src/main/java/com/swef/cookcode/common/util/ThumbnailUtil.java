package com.swef.cookcode.common.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.InputStream;

public class ThumbnailUtil {

    public static void extractFirstFrame(InputStream inputStream, String outputImagePath) throws FrameGrabber.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        grabber.start();

        Frame frame = grabber.grabImage();

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Mat mat = converter.convert(frame);
        opencv_imgcodecs.imwrite(outputImagePath, mat);

        grabber.stop();
        grabber.release();
    }
}
