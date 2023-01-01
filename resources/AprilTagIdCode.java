/*
 This file is not an FTC OpMode.
 It contains methods to detect and process AprilTag ID codes in camera images.
 
 The methods here can be called by a Java OpMode, and are annotated to become
 myBlocks in an FTC Blocks OpMode.

 Supports webcam.  For internal RC phone camera, make 3 simple edits as shown,
 then click "Build Everything" (wrench icon).

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

public class AprilTagIdCode
{

    // Declare class for holder object to contain pipeline & camera instances
    public static class BlocksContext
    {
        AprilTagDetectionPipeline pipeline;
        
        // EDIT #1 FOR RC PHONE (internal camera), start next line with "//"
        OpenCvWebcam camera;

        // for RC phone cam, remove "//" from next line
        // OpenCvCamera camera;

    }

    // embedded/dummy pipeline parameters; see note below
    static double tagSize = .05;
    static double fx = 1000;
    static double fy = 1000;
    static double cx = 200;
    static double cy = 200;
    

    // this annotation creates the myBlock "createAprilTagDetector"
    @ExportToBlocks ( 
        comment = "Create a pipeline/camera object for AprilTag ID code detection. " +
                  "If used, webcam name must be 'Webcam 1' in configuration.  Use this " +
                  "myBlock in INIT section of OpMode, before startAprilTagDetector.",
        tooltip = "Create an AprilTag ID code detection pipeline/camera object",

        // EDIT #2 FOR ROBOT CONTROLLER PHONE, start next line with "//"
        heading = "FOR WEBCAM ONLY"
        
        // for RC phone cam, remove "//" from next line
        // heading = "FOR RC PHONE ONLY (internal camera)"

        )
    public static BlocksContext createAprilTagDetector(HardwareMap hardwareMap)
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier
            ("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // create a holder object containing a pipeline instance & camera instance
        BlocksContext ctx = new BlocksContext();

        // Identify the camera.
        // EDIT #3 FOR ROBOT CONTROLLER PHONE, start next line with "//"
        ctx.camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);   

        // for RC phone cam, remove "//" from next line
        // ctx.camera = OpenCvCameraFactory.getInstance().createInternalCamera2(OpenCvInternalCamera2.CameraDirection.BACK, cameraMonitorViewId);

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


    // this annotation creates the myBlock "startAprilTagDetector"
    @ExportToBlocks(
        comment = "Begin operating camera/stream/pipeline for AprilTag detection. " +
                   "Must specify a resolution supported by the camera. " +
                   "Use this myBlock in INIT section of OpMode, after createAprilTagDetector.",
        tooltip = "Begin operating AprilTag detection camera/stream/pipeline",
        parameterLabels = {"AprilTagDetector", "width (pixels)", "height (pixels)"},
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


    // this annotation creates the myBlock "closeAprilTagDetector"
    @ExportToBlocks(
        comment = "Close/disable the designated AprilTag pipeline, to free up " +
                  "CPU resources. Use this after saving all needed AprilTag info.",
        tooltip = "Close the AprilTag pipeline",
        parameterLabels = {"AprilTag Detector"}
    )
    public static void closeAprilTagDetector(BlocksContext ctx)
    {
        // Close access to camera. This is faster than synchronous closeCameraDevice().
        ctx.camera.closeCameraDeviceAsync(new OpenCvCamera.AsyncCameraCloseListener() {
            
            @Override
            public void onClose() {
             
            }
        });
        
    }   // end method closeAprilTagDetector()

}   // end class AprilTagIdCode
