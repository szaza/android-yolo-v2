# Android YOLOv2 with tensorflow mobile
This android application uses YOLOv2 model for object detection. It uses tensorflow mobile to run neural networks, later I would like to use tensorflow lite. Probably, it is the first implementation of YOLOv2 for Tensorflow on Android device. The demo application detects the 20 classes of objects in Pascal VOC dataset. The network outputs for VOC a 13x13x125 tensor - 13x13 bounding boxes with 5 coordinates each and 20 classes per box so it means 125 filters.

**Steps to use this demo:**
* Clone this repository
* Imort your project into the Android Studio
* Optional: put your protobuff file and labels.txt under the assets folder, then change the settings properly in the [Config.java](https://github.com/szaza/android-yolov2/blob/master/src/org/tensorflow/demo/Config.java) file.
* Run project

Please read the paper for more information about the YOLOv2 model: [YOLO9000 Better, Faster, Stronger](https://arxiv.org/pdf/1612.08242.pdf)
