# Android YOLO with tensorflow mobile
This android application uses YOLOv2 model for object detection. It uses tensorflow mobile to run neural networks. I would like to use tensorflow lite later. Probably, it is the first open source implementation of the second version of YOLO for Tensorflow on Android device. The demo application detects 20 classes of Pascal VOC dataset.

**Steps to use this demo:**
* Clone this repository
* Imort your project into the Android Studio
* Optional: put your protobuff file and labels.txt under the assets folder, then change the settings properly in the [Config.java](https://github.com/szaza/android-yolov2/blob/master/src/org/tensorflow/demo/Config.java) file.
* Run project

Please read the paper for more information about the YOLOv2 model: [YOLO9000 Better, Faster, Stronger](https://arxiv.org/pdf/1612.08242.pdf)

Android 6.0 (API level 23) or higher is required to run the demo application due to usage of Camera2 API.

How it works?

![android yolo v2 sample image](https://github.com/szaza/android-yolo-v2/blob/master/sample/android-yolo-v2.png)
![android yolo v2 sample image](https://github.com/szaza/android-yolo-v2/blob/master/sample/android-yolo-v2.1.png)

If you would like a more accurate solution, create a server application. See my next projects here:
* [Tensorflow Java Example server application with YOLOv2 model](https://github.com/szaza/tensorflow-example-java)
* [Tensorflow Java Tutorial with Spring and Gradle](https://github.com/szaza/tensorflow-java-examples-spring)
