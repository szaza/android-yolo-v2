package org.tensorflow.yolo.model;

import java.util.List;

/**
 * Model to store the outcome from post-processing
 *
 * Created by Chew Jing Wei on 21/01/21.
 */
public class PostProcessingOutcome {
    private long postProcessingTime;
    private List<Recognition> recognitions;

    public PostProcessingOutcome(long postProcessingTime, List<Recognition> recognitions) {
        this.setPostProcessingTime(postProcessingTime);
        this.setRecognitions(recognitions);
    }

    public long getPostProcessingTime() {
        return this.postProcessingTime;
    }

    public void setPostProcessingTime(long postProcessingTime) {
        this.postProcessingTime = postProcessingTime;
    }

    public List<Recognition> getRecognitions() {
        return this.recognitions;
    }

    public void setRecognitions(List<Recognition> recognitions) {
        this.recognitions = recognitions;
    }
}
