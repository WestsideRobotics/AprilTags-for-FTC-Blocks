/*
 This file is not an FTC OpMode.
 It contains methods to detect and process AprilTag ID codes in camera images.
 
Supports webcam and internal RC phone camera.
 
 The methods are annotated to become myBlocks, for FTC Blocks programming.

 This Java code was adapted for the wiki tutorial from an original file
 AprilTagBlocksBridge.java shared by @Windwoes.  Key changes include:
 - add webcam, allow name editing
 - add method to close pipeline
 - expand annotations
 - remove navigation/pose methods
 - embed camera lens intrinsics

 Questions, comments and corrections to westsiderobotics@verizon.net

*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvInternalCamera2;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;

public class AprilTagIdCodeBlocks
{

    // Declare class for holder object to contain pipeline & camera instances
    public static class BlocksContext
    {
        AprilTagDetectionPipeline pipeline;
        OpenCvCamera camera;
        OpenCvWebcam webcam;

    }

    // embedded/dummy pipeline parameters; see note below
    static double tagSize = .05;
    static double fx = 1000;
    static double fy = 1000;
    static double cx = 200;
    static double cy = 200;
    

    // this annotation creates the myBlock "createAprilTagDetectorWebcam"
    @ExportToBlocks ( 
        heading = "Create pipeline, for webcam only",
        comment = "Create a pipeline/webcam object for AprilTag ID code detection. " +
                  "Specify name of webcam in active configuration.  Use this myBlock " +
                  "in INIT section of OpMode, before startAprilTagDetector.",
        tooltip = "Create an AprilTag ID code detection pipeline/webcam object",
        parameterLabels = {"null", "Configured webcam name"},
        parameterDefaultValues = {"hardwareMap", "Webcam 1"}
        )
    public static BlocksContext createAprilTagDetectorWebcam(HardwareMap hardwareMap,
        String webcamName)
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier
            ("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // create a holder object containing a pipeline instance & webcam instance
        BlocksContext ctx = new BlocksContext();

        // identify the configured webcam
        ctx.webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get
            (WebcamName.class, webcamName), cameraMonitorViewId);   

        // Create the pipeline.  See note below.
        ctx.pipeline = new AprilTagDetectionPipeline(tagSize, fx, fy, cx, cy);

        // provide the pipeline/camera object to the OpMode
        return ctx;

        /*
        The above 5 parameters can be used to calculate pose (position and orientation).
        They are not exposed for Blocks use, for this tutorial focused on detecting an 
        AprilTag ID code.

        Tag size is measured along one edge, in meters.
    
        fx, fy, cx and cy are 'lens intrinsics' obtained from camera calibration
        and choice of resolution.  See examples in https://github.com/FIRST-Tech-Challenge/
        FtcRobotController/blob/master/TeamCode/src/main/res/xml/teamwebcamcalibrations.xml
        */

    }   // end method createAprilTagDetectorWebcam()


    // this annotation creates the myBlock "createAprilTagDetectorPhoneCam"
    @ExportToBlocks ( 
        heading = "Create pipeline, for RC phone camera",
        comment = "Create a pipeline/camera object for AprilTag ID code detection. " +
                  "Use this for internal Robot Controller phone camera, not webcam.  Place " +
                  "this myBlock in INIT section of OpMode, before startAprilTagDetector.",
        tooltip = "Create an AprilTag ID code detection pipeline/webcam object"
        )
    public static BlocksContext createAprilTagDetectorPhoneCam(HardwareMap hardwareMap)
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier
            ("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // create a holder object containing a pipeline instance & camera instance
        BlocksContext ctx = new BlocksContext();

        // identify the Robot Controller phone's rear camera
        ctx.camera = OpenCvCameraFactory.getInstance().createInternalCamera2(OpenCvInternalCamera2.CameraDirection.BACK, cameraMonitorViewId);

        // Create the pipeline.  See note below.
        ctx.pipeline = new AprilTagDetectionPipeline(tagSize, fx, fy, cx, cy);

        // provide the pipeline/camera object to the OpMode
        return ctx;

        /*
        The above 5 parameters can be used to calculate pose (position and orientation).
        They are not exposed for Blocks use, for this tutorial focused on detecting an 
        AprilTag ID code.

        Tag size is measured along one edge, in meters.
    
        fx, fy, cx and cy are 'lens intrinsics' obtained from camera calibration
        and choice of resolution.  See examples in https://github.com/FIRST-Tech-Challenge/
        FtcRobotController/blob/master/TeamCode/src/main/res/xml/teamwebcamcalibrations.xml
        */

    }   // end method createAprilTagDetectorPhoneCam()


    // this annotation creates the myBlock "startAprilTagDetectorWebcam"
    @ExportToBlocks(
        heading = "Start pipeline, for webcam only",
        comment = "Begin operating webcam/stream/pipeline for AprilTag detection. " +
                   "Must specify a resolution supported by the webcam. " +
                   "Use this myBlock in INIT section of OpMode, after createAprilTagDetector.",
        tooltip = "Begin operating AprilTag detection webcam/stream/pipeline",
        parameterLabels = {"AprilTagDetector", "width (pixels)", "height (pixels)"},
        parameterDefaultValues = {"null", "640", "480"}
        )
    public static void startAprilTagDetectorWebcam(BlocksContext ctx, int width, int height)
    {
        // receive the pipeline and open/access the camera
        ctx.webcam.setPipeline(ctx.pipeline);
        ctx.webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {

            @Override
            public void onOpened() {
    
                // begin Camera Stream at the specified resolution
                ctx.webcam.startStreaming(width, height);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }   // end method startAprilTagDetectorWebcam()


    // this annotation creates the myBlock "startAprilTagDetectorPhoneCam"
    @ExportToBlocks(
        heading = "Start pipeline, for RC phone camera",
        comment = "Begin operating camera/stream/pipeline for AprilTag detection. " +
                   "Must specify a resolution supported by the RC phone camera. " +
                   "Use this myBlock in INIT section of OpMode, after createAprilTagDetector.",
        tooltip = "Begin operating AprilTag detection camera/stream/pipeline",
        parameterLabels = {"AprilTagDetector", "width (pixels)", "height (pixels)"},
        parameterDefaultValues = {"null", "640", "480"}
        )
    public static void startAprilTagDetectorPhoneCam(BlocksContext ctx, int width, int height)
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
    }   // end method startAprilTagDetectorPhoneCam()


    // this annotation creates the myBlock "getAllDetections"
    @ExportToBlocks(
         comment = "Provide the data from any and all detetected AprilTags. " +
                  "Use this myBlock anywhere in the OpMode, after startAprilTagDetector. ",
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
                  "Use this myBlock anywhere in the OpMode, after getAllDetections. ",
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
                  "AprilTag detected.  Use it anywhere, after getAllDetections. " +
                  "This will crash if the input detections list is empty.",
        tooltip = "Get data from a single detected AprilTag",
        parameterLabels = {"AprilTagDetections", "index"},
        parameterDefaultValues = {"null", "0"}      // 0 for first set of data
        )
    public static AprilTagDetection getOneDetection
        (ArrayList<AprilTagDetection> detections, int index)
    {
        // Provide data for a single detection, designated by the index number.
        // Requires a non-null input detections array (list cannot be empty).
        return detections.get(index);
    }   // end method getOneDetection()


    // this annotation creates the myBlock "getID"
    @ExportToBlocks(
        comment = "Provide the AprilTag ID code from the designated detection. " +
                  "Use this myBlock anywhere, after getOneDetection.",
        tooltip = "Get AprilTag ID code",
        parameterLabels = {"Detection"},
        color = 155     // green for property Block (not a function)
        )
    public static double getID(AprilTagDetection detection)
    {
        // provide the AprilTag ID code from the designated detection
        return detection.id;
    }   // end method getID()


    // this annotation creates the myBlock "closeAprilTagDetectorWebcam"
    @ExportToBlocks(
        heading = "Close pipeline, for webcam only",
        comment = "Close/disable the designated AprilTag pipeline, to free up " +
                  "CPU resources. Use this after saving all needed AprilTag info.",
        tooltip = "Close the AprilTag pipeline",
        parameterLabels = {"AprilTag Detector"}
    )
    public static void closeAprilTagDetectorWebcam(BlocksContext ctx)
    {
        // Close access to webcam. This is faster than synchronous closeCameraDevice().
        ctx.webcam.closeCameraDeviceAsync(new OpenCvCamera.AsyncCameraCloseListener() {
            
            @Override
            public void onClose() {
             
            }
        });
        
    }   // end method closeAprilTagDetectorWebcam()



    // this annotation creates the myBlock "closeAprilTagDetectorPhoneCam"
    @ExportToBlocks(
        heading = "Close pipeline, for RC phone camera",
        comment = "Close/disable the designated AprilTag pipeline, to free up " +
                  "CPU resources. Use this after saving all needed AprilTag info.",
        tooltip = "Close the AprilTag pipeline",
        parameterLabels = {"AprilTag Detector"}
    )
    public static void closeAprilTagDetectorPhoneCam(BlocksContext ctx)
    {
        // Close access to camera. This is faster than synchronous closeCameraDevice().
        ctx.camera.closeCameraDeviceAsync(new OpenCvCamera.AsyncCameraCloseListener() {
            
            @Override
            public void onClose() {
             
            }
        });
        
    }   // end method closeAprilTagDetectorPhoneCam()


}   // end class AprilTagIdCodeBlocks

