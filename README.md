# Android YOLO with TensorFlow Mobile
This android application uses YOLOv2 model for object detection. It uses tensorflow mobile to run neural networks. I would like to use tensorflow lite later. Probably, it is the first open source implementation of the second version of YOLO for Tensorflow on Android device. The demo application detects 20 classes of Pascal VOC dataset. Please read this paper for more information about the YOLOv2 model: [YOLO9000 Better, Faster, Stronger](https://arxiv.org/pdf/1612.08242.pdf).

**Train YOLO for your own dataset**

Please find more information about retraining the model on my site: https://sites.google.com/view/tensorflow-example-java-api/complete-guide-to-train-yolo. I've also added several Google Colab interactive sample for the step-by-step tutorial, so the training process can be tried out on Google virtual machines.

**Steps to compile and run the application:**

Prerequirements:

* Install the [Android Studio](https://developer.android.com/studio/index.html);
* Android 6.0 (API level 23) or higher is required to run the demo application due to usage of Camera2 API;

Compile and run the project:

* Clone this repository with command: `git clone https://github.com/szaza/android-yolo-v2.git`;
* Imort your project into the Android Studio;
* Optional: put your protobuff file and labels.txt into the assets folder, then change the settings properly in the [Config.java](https://github.com/szaza/android-yolo-v2/blob/master/src/org/tensorflow/yolo/Config.java) file;
* Run the project from Android Studio;

How it works?

![android yolo v2 sample image](https://github.com/szaza/android-yolo-v2/blob/master/sample/android-yolo-v2.png)
![android yolo v2 sample image](https://github.com/szaza/android-yolo-v2/blob/master/sample/android-yolo-v2.1.png)

If you would like a more accurate solution, create a server application. See my related projects here:
* [Tensorflow Example Java API](https://sites.google.com/view/tensorflow-example-java-api/home)
* [Tensorflow Java example server application with YOLOv2 model](https://sites.google.com/view/tensorflow-example-java-api/tensorflow-java-api-with-spring-framework)
