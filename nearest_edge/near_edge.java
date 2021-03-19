package nearest_edge;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.PointRoi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;


import java.awt.*;
import java.util.ArrayList;

public class near_edge implements PlugIn {

    @Override
    public void run(String s) {



        ImagePlus imp = WindowManager.getCurrentImage();
        ImageStack ims = imp.getStack();
        PointRoi roi = (PointRoi) imp.getRoi();
        Point[] rois = roi.getContainedPoints();


        ResultsTable rt = new ResultsTable();
        Analyzer.setResultsTable(rt);

        for (int n = 0; n < ims.getSize(); n++) {
            ArrayList<Integer> x = new ArrayList<>();
            ArrayList<Integer> y = new ArrayList<>();

            ImageProcessor ip = ims.getProcessor(n + 1);

            for (int i = 0; i < ip.getWidth(); i++) {
                for (int j = 0; j < ip.getHeight(); j++) {
                    if (ip.getPixel(i, j) > 0) {
                        x.add(i);
                        y.add(j);
                    }
                }
            }

            double[] dists = new double[rois.length];
            double[][] direction = new double[rois.length][2];

            for (int i = 0; i < rois.length; i++) {
                double roix = rois[i].getX();
                double roiy = rois[i].getY();
                double mindist = 10000;
                for (int j = 0; j < x.size(); j++) {
                    double d = Math.sqrt(Math.pow(roix - x.get(j), 2) + Math.pow(roiy - y.get(j), 2));
                    if (d < mindist) {
                        mindist = d;
                        direction[i][0] = Math.signum(roix - x.get(j));
                        direction[i][1] = Math.signum(roiy - y.get(j));
                    }
                }
                dists[i] = mindist;

            }


            for (int i = 0; i < dists.length; i++) {

                if (n == 0) {
                    rt.setValue("x", i, rois[i].getX());
                    rt.setValue("y", i, rois[i].getY());

                    //rt.addValue("direction1", direction[i][0]);
                    //rt.addValue("direction2", direction[i][1]);
                }
                rt.setValue("dist_" + (n + 1), i, dists[i]);


            }
        }

        rt.show("Results");


    }
}
