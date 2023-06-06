package com.swef.cookcode.common.util;

import com.swef.cookcode.common.error.exception.ThumbnailException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.*;

import static com.swef.cookcode.common.ErrorCode.FFMPEGFRAMEGRABBER_FAILED;

public class ThumbnailUtil {

    public static InputStream extractFirstFrame(InputStream inputStream){

        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
            grabber.start();

            Frame frame = grabber.grabImage();

            grabber.stop();
            grabber.release();

            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

            Mat mat = converter.convert(frame);

            return matToInputStream(mat);

        } catch (FFmpegFrameGrabber.Exception e) {
            throw new ThumbnailException(FFMPEGFRAMEGRABBER_FAILED);
        }
    }


    private static InputStream matToInputStream(Mat mat) {
        byte[] byteArray = new byte[mat.size().area()];

        opencv_imgcodecs.imencode(".png", mat, byteArray);

        return new ByteArrayInputStream(byteArray);
    }
}
