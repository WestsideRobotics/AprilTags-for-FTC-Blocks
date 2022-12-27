
// This is a simplified version of AprilTagBlocksBridge for webcam.


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;

public class AprilTagSimpleBlocksWebcam
{
    public static class BlocksContext
    {
        AprilTagDetectionPipeline pipeline;
        OpenCvWebcam camera;
    }


    // this annotation creates the myBlock "createAprilTagDetector"
    @ExportToBlocks ( 
        comment = "Create a pipeline for AprilTag detection.  Use this Block " +
                  "in the INIT section of the OpMode, before startAprilTagDetector. " +
                  "Parameter values are not critical for simple tag ID detection.",
        tooltip = "Create an AprilTag detection pipeline",
        parameterLabels = {"hardwareMap", "tag size (mm)", "fx", "fy", "cx", "cy"},
        parameterDefaultValues = {"null", "75", "1000", "1000", "200", "200"}
        )
    public static BlocksContext createAprilTagDetector(HardwareMap hardwareMap,
        double tagSizeMm, double fx, double fy, double cx, double cy)
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier
            ("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // Tag size (millimeters) is measured along one edge.  OpenCV uses meters.
        double tagSizeMeter = tagSizeMm / 1000;
        
        BlocksContext ctx = new BlocksContext();

        // identify the configured webcam
        ctx.camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get
            (WebcamName.class, "Webcam 1"), cameraMonitorViewId);   

        // create the pipeline
        ctx.pipeline = new AprilTagDetectionPipeline(tagSizeMeter, fx, fy, cx, cy);

        // fx, fy, cx and cy are 'lens intrinsics' obtained from camera calibration
        // and choice of resolution.  Needed for pose info (position and orientation).

        // Examples from https://github.com/FIRST-Tech-Challenge/FtcRobotController/
        // blob/master/TeamCode/src/main/res/xml/teamwebcamcalibrations.xml
    
        // Logitech HD webcam C270 at 640 x 480
        // Focal length fx = 822.317, fy = 822.317
        // Principal point cx = 319.495, cy = 242.502

        // Logitech webcam C920 at 800 x 448
        // Focal length fx = 578.272, fy = 578.272
        // Principal point cx = 402.145, cy = 221.506

        // provide the pipeline to the OpMode
        return ctx;
        
    }   // end method createAprilTagDetector()


    // this annotation creates the myBlock "startAprilTagDetector"
    @ExportToBlocks(
         comment = "Begin operating the pipeline for AprilTag detection.  Use this Block " +
                   "in the INIT section of the OpMode, after createAprilTagDetector. " +
                   "Must use a resolution supported by the webcam.",
        tooltip = "Begin operating an AprilTag detection pipeline",
        parameterLabels = {"AprilTagDetector", "width", "height"},
        parameterDefaultValues = {"null", "640", "480"}
        )
    public static void startAprilTagDetector(BlocksContext ctx, int width, int height)
    {
        // receive the pipeline and open/access the camera
        ctx.camera.setPipeline(ctx.pipeline);
        ctx.camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {

            @Override
            public void onOpened() {
    
                // begin Camera Stream at the specified resolution
                ctx.camera.startStreaming(width, height);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }   // end method startAprilTagDetector()


    // this annotation creates the myBlock "getAllDetections"
    @ExportToBlocks(
         comment = "Provide the data from any and all detetected AprilTags. " +
                  "Use this Block anywhere in the OpMode, after startAprilTagDetector. ",
        tooltip = "Get data from all detected AprilTags",
        parameterLabels = {"AprilTagDetector"}
        )
    public static ArrayList<AprilTagDetection> getAllDetections(BlocksContext ctx)
    {
        return ctx.pipeline.getLatestDetections();
    }   // end method getAllDetections()


    // this annotation creates the myBlock "getHowManyDetections"
    @ExportToBlocks ( 
        comment = "Provide the number of detections in the current batch. " +
                  "Use this Block anywhere in the OpMode, after getAllDetections. ",
        tooltip = "Get the number of current detections",
        parameterLabels = {"AprilTagDetections"},
        color = 155     // green for property Block (not a function)
        )
    public static int getHowManyDetections(ArrayList<AprilTagDetection> detections)
    {
        if(detections == null)
        {
            return 0;
        }
        return detections.size();
    }   // end method getHowManyDetections()


    // this annotation creates the myBlock "getOneDetection"
    @ExportToBlocks(
        comment = "Provide the data from only the first detection in the current " +
                  "batch.  This myBlock is helpful when you know there's only one " +
                  "AprilTag detected.  Use it anywhere, after getAllDetections.",
        tooltip = "Get data from a single detected AprilTag",
        parameterLabels = {"AprilTagDetections", "index"},
        parameterDefaultValues = {"null", "0"}      // 0 for first set of data
        )
    public static AprilTagDetection getOneDetection
        (ArrayList<AprilTagDetection> detections, int index)
    {
        // provide data for a single detection, designated by the index number
        return detections.get(index);
    }   // end method getOneDetection()


    // this annotation creates the myBlock "getID"
    @ExportToBlocks(
        comment = "Provide the AprilTag ID code from the designated detection. " +
                  "Use this anywhere, after getOneDetection.",
        tooltip = "Get AprilTag ID code",
        parameterLabels = {"Detection"},
        color = 155     // green for property Block (not a function)
        )
    public static double getID(AprilTagDetection detection)
    {
        // provide the AprilTag ID code from the designated detection
        return detection.id;
    }   // end method getID()


    // this annotation creates the myBlock "closeAprilTagDetector"
    @ExportToBlocks(
        comment = "Close/disable the designated AprilTag pipeline, to free up " +
                  "CPU resources. Use this after saving all needed AprilTag info.",
        tooltip = "Close the AprilTag pipeline",
        parameterLabels = {"AprilTag Detector"}
    )
    public static void closeAprilTagDetector(BlocksContext ctx)
    {
        // close access to the webcam
        ctx.camera.closeCameraDevice();
    }   // end method closeAprilTagDetector()


}   // end class AprilTagSimpleBlocksWebcam

