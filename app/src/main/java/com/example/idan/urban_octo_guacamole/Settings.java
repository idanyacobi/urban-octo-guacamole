package com.example.idan.urban_octo_guacamole;

import org.opencv.core.Size;
import static org.opencv.core.CvType.CV_8UC1;

class Settings {

    static final int ENV_SIZE = 13;
    static final int IMAGE_CVTYPE = CV_8UC1;
    static final int DESC_SIZE = 36;
    static final int WIDTH_SIZE = 128;
    static final int HEIGHT_SIZE = 128;
    static final int WINDOW_SIZE = 4;
    static final int STEP_OVERLAP = 1;
    static final int BLOCK_SIZE = 4;
    static final int CELL_SIZE = 2;
    static final int PADDING_SIZE = 0;
    static final int PATCH_SIZE = 4;
    static final int OVERLAP_SIZE = PATCH_SIZE / STEP_OVERLAP;
    static final Size IMAGE_SIZE = new Size(WIDTH_SIZE, HEIGHT_SIZE);
}
