package com.example.idan.urban_octo_guacamole;

import android.util.SparseIntArray;

import com.example.idan.urban_octo_guacamole.triangulation.DelaunayTriangulator;
import com.example.idan.urban_octo_guacamole.triangulation.NotEnoughPointsException;
import com.example.idan.urban_octo_guacamole.triangulation.Triangle2D;
import com.example.idan.urban_octo_guacamole.triangulation.Vector2D;

import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idan on 04/04/2017.
 */



public class depthToMesh {

    Mat depth;
    List<Vector2D> points;
    Map<Integer, Vector2D> pointsMap;
    DelaunayTriangulator DT;
    int THRESHOLD = 15;
    SparseIntArray trans;
    List<Triangle2D> triangles;
    Size s;


    public depthToMesh(Mat depth) {
        this.depth = depth;
        s = this.depth.size();
//        this.points = getPointsFromDepth();
        getPointsMapFromDepth();
        this.triangles = YacobiTriangulate();
        this.DT = new DelaunayTriangulator(points);
        try {
            DT.triangulate();
        } catch (NotEnoughPointsException e) {}

        System.out.println("Done constructing");
    }


    public List<Triangle2D> YacobiTriangulate() {
        List<Triangle2D> triangles = new ArrayList<Triangle2D>();
        int npoints = this.trans.size();
        for (int i = 0; i < npoints; i++) {
            Vector2D p = new Vector2D(i % s.width, i / s.width);
            triangles.addAll(get_triangles(p));
            if (i % 5000 == 0) {
                System.out.println(i + " vertices colored");
            }
        }
        return triangles;
    }

    private List<Triangle2D> get_triangles(Vector2D p) {
        List<Triangle2D> triangles = new ArrayList<Triangle2D>();
        Vector2D[] dirs = {new Vector2D(-1,-1), new Vector2D(1,1)};
        for (Vector2D d: dirs) {
            int i = trans.get(getPixelIdx(p.x, p.y, s.width));
            int i1 = trans.get(getPixelIdx(p.x, p.y + d.y, s.width));
            int i2 = trans.get(getPixelIdx(p.x + d.x, p.y, s.width));
        }
        return triangles;
    }

    private int getPixelIdx(double x, double y, double width) {
        return (int) (x + y * width);
    }


    private List<Vector2D> getPointsFromDepth() {
        MinMaxLocResult mmlr = Core.minMaxLoc(this.depth);
        List<Vector2D> points = new ArrayList<Vector2D>();
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] pixel = this.depth.get(i, j);
                if (pixel[0] > mmlr.minVal + THRESHOLD) {
                    points.add(new Vector2D(j, i));
                }
            }
        }
        return points;
    }

    private void getPointsMapFromDepth() {
        MinMaxLocResult mmlr = Core.minMaxLoc(this.depth);
        Map<Integer, Vector2D> points = new HashMap<Integer, Vector2D>();
        this.trans = new SparseIntArray();
        int matIdx = 0;
        int ptsIdx = 0;
        for (int i = 0; i < s.height; i++) {
            for (int j = 0; j < s.width; j++) {
                double[] pixel = this.depth.get(i, j);
                if (pixel[0] <= mmlr.minVal + THRESHOLD) {
                    matIdx += 1;
                    continue;
                }
                this.trans.put(matIdx, ptsIdx);
                matIdx++;
                ptsIdx++;
            }
        }
    }

}
