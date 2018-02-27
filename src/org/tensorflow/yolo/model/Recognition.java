package org.tensorflow.yolo.model;

/**
 * An immutable result returned by a recognizer describing what was recognized.
 * Created by Zoltan Szabo on 12/17/17.
 * URL: https://github.com/szaza/android-yolo-v2
 */
public final class Recognition {
    /**
     * A unique identifier for what has been recognized. Specific to the class, not the instance of
     * the object.
     */
    private final Integer id;
    private final String title;
    private final Float confidence;
    private BoxPosition location;

    public Recognition(final Integer id, final String title,
                       final Float confidence, final BoxPosition location) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getConfidence() {
        return confidence;
    }

    public BoxPosition getLocation() {
        return new BoxPosition(location);
    }

    public void setLocation(BoxPosition location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Recognition{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", confidence=" + confidence +
                ", location=" + location +
                '}';
    }
}
