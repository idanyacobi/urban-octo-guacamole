package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Size;
import static org.opencv.core.CvType.CV_8UC1;

class Settings {

    static final int PATCH_SIZE = 32;
    static final int DESC_LENGTH = 36;
    static final int ENV_SIZE = 33;
    static final Size IMAGE_SIZE = new Size(128, 128);
    static final int IMAGE_CVTYPE = CV_8UC1;
}
