package Neigbours;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import myClasses.Point3D;
import myClasses.getPointRois;

import java.awt.*;
import java.util.ArrayList;

public class Nearest_Neighbours implements PlugIn {


    coordinates c;

    public void run(String s){

        Boolean strict, single;
        ArrayList<pair> pairset = null;
        double[] x,y;
        double[] x2 =null;
        double[] y2 =null;

        int[] idList = WindowManager.getIDList();
        String[] titles = new String[idList.length+1];
        int ID1 = 0;
        int ID2 = 0;

        for(int i=0;i<idList.length;i++){
            titles[i] = WindowManager.getImage(idList[i]).getTitle();
        }

        titles[idList.length] = "none";

        //dialogbox
        GenericDialog gd = new GenericDialog("Neighbour settings");
        gd.addChoice("Image_1", titles, titles[idList.length]);
        gd.addChoice("Image_2", titles,titles[idList.length]);
        gd.addCheckbox("single image",true);
        gd.addCheckbox("strickt pairs",false);
        gd.showDialog();

        if(gd.wasCanceled()){
            return;
        }
        ID1 = gd.getNextChoiceIndex();
        ID2 = gd.getNextChoiceIndex();
        single = gd.getNextBoolean();
        strict = gd.getNextBoolean();


        if(!single && ID1 == idList.length && ID2 == idList.length ){
            return;
        }
        if(single && ID1 == idList.length){
            return;
        }


        ImagePlus imp = WindowManager.getImage(idList[ID1]);



        Point3D[] pta;
        try {
            pta = new getPointRois(imp).getRois();
        }catch(IndexOutOfBoundsException e){
            ResultsTable rt = new ResultsTable();
            Analyzer.setResultsTable(rt);
            rt.show("Results");
            IJ.log("no point selection present");
            return;
        }

        x = new double[pta.length];
        y = new double[pta.length];

        for(int i=0;i<pta.length;i++){
            x[i] = pta[i].getX();
            y[i] = pta[i].getY();

        }

        if(!single){
            imp = WindowManager.getImage(idList[ID2]);

            try {
                pta = new getPointRois(imp).getRois();
            }catch(IndexOutOfBoundsException e){
                ResultsTable rt = new ResultsTable();
                Analyzer.setResultsTable(rt);
                rt.show("Results");
                IJ.log("no point selection present");
                return;
            }

            x2 = new double[pta.length];
            y2 = new double[pta.length];

            for(int i=0;i<pta.length;i++){
                x2[i] = pta[i].getX();
                y2[i] = pta[i].getY();
            }
        }

        if(single){
            this.c = new coordinates(x,y);
        }else{
            this.c = new coordinates(x,y,x2,y2);
        }

        if(strict){
            if(single) {
                pairset = getNNstrickt(c);
            }else{
                pairset = getNNstrickt_twosets(c);
            }
        }else{
            if(single) {
                pairset = getNNloose(c);
            }else{
                pairset = getNNloose_twosets(c);
            }
        }

        ResultsTable rt = Analyzer.getResultsTable();

        if(rt==null) {
            rt = new ResultsTable();
        }

        rt.reset();



        for(pair p : pairset){

            rt.incrementCounter();
            rt.addValue("x1",p.x1);
            rt.addValue("y1",p.y1);
            rt.addValue("x2",p.x2);
            rt.addValue("y2",p.y2);
            rt.addValue("index1",p.i1);
            rt.addValue("index2",p.i2);
            rt.addValue("Distance",p.dist);

        }

        Analyzer.setResultsTable(rt);
        rt.updateResults();
        rt.show("Results");





    }


    //methods for single point set


    private ArrayList<pair> getNNstrickt(coordinates c){


        ArrayList<pair> pairs = new ArrayList<>();

        if(c.x.length<=1){

            if(c.x.length==1) {
                pairs.add(new pair(c.x[0], c.y[0], 0, 0, -1, -1));
            }else{
                pairs.add(new pair(0, 0, 0, 0, -1, -1));
            }

            return pairs;
        }

        boolean[] assigned = new boolean[c.x.length];
        int[] minRow,minCol;
        boolean test = false;
        int cnt;




        while(!test) {
            minRow = getMinRow(c.distmat, assigned);
            minCol = getMinCol(c.distmat, assigned);

            for (int i = 0; i < minRow.length; i++) {
                if (minCol[minRow[i]] == i && !assigned[i]) {
                    assigned[i] = true;
                    assigned[minRow[i]] = true;
                    pairs.add(new pair(c.x[i],c.y[i],c.x[minRow[i]],c.y[minRow[i]],i,minRow[i]));
                }
            }

            cnt = 0;

            for (boolean i:assigned) {
                if (!i) {
                    cnt++;
                }
            }

            if(cnt<=1){
                test=true;
            }

            IJ.showProgress(assigned.length-cnt,assigned.length-1);

        }

        return pairs;


    }

    private ArrayList<pair> getNNloose(coordinates c){


        ArrayList<pair> pairs = new ArrayList<>();

        if(c.x.length<=1){

            if(c.x.length==1) {
                pairs.add(new pair(c.x[0], c.y[0], 0, 0, -1, -1));
            }else{
                pairs.add(new pair(0, 0, 0, 0, -1, -1));
            }

            return pairs;
        }

        boolean[] assigned = new boolean[c.x.length];
        int[] minRow;


        minRow = getMinRow(c.distmat, assigned);


        for (int i = 0; i < minRow.length; i++) {


                pairs.add(new pair(c.x[i],c.y[i],c.x[minRow[i]],c.y[minRow[i]],i,minRow[i]));

                IJ.showProgress(i,minRow.length-1);

        }

        return pairs;



    }



    private int[] getMinRow(double[][] distmat, boolean[] assigned){

        int[] minRow = new int[distmat.length];
        double mindist;

        for(int i=0;i<distmat.length;i++){

            mindist = c.max+1;
            for(int j=0;j<distmat[i].length;j++){

                if(i!=j && !assigned[i] && !assigned[j]){
                    if(distmat[i][j]<mindist){
                        mindist = distmat[i][j];
                        minRow[i] = j;

                    }
                }
            }
        }


        return minRow;

    }

    private int[] getMinCol(double[][] distmat, boolean[] assigned){

        int[] minCol = new int[distmat[0].length];
        double mindist;

        for(int i=0;i<distmat[0].length;i++){
            mindist = c.max+1;
            for(int j=0;j<distmat.length;j++){
                if(i!=j && !assigned[i] && !assigned[j]){
                    if(distmat[j][i]<mindist){
                        mindist = distmat[i][j];
                        minCol[i] = j;
                    }
                }
            }
        }

        return minCol;

    }

    //methods for dual point set



    private ArrayList<pair> getNNloose_twosets(coordinates c){




        ArrayList<pair> pairs = new ArrayList<>();

        if(c.x.length < 1 || c.x2.length <1){
            pairs.add(new pair(0,0,0,0,-1,-1));
            return pairs;
        }

        boolean[] assigned_row = new boolean[c.x.length];
        boolean[] assigned_col = new boolean[c.x2.length];

        int[] minRow = getMinRow(c.distmat, assigned_row, assigned_col);
        int[] minCol = getMinCol(c.distmat, assigned_row, assigned_col);

        for (int i = 0; i < minRow.length; i++) {

            assigned_row[i] = true;
            assigned_col[minRow[i]] = true;
            pairs.add(new pair(c.x[i],c.y[i],c.x2[minRow[i]],c.y2[minRow[i]],i,minRow[i]));

            IJ.showProgress(i,minRow.length-1);

        }

        for (int i = 0; i < minCol.length; i++) {

            if(!assigned_col[i]) {
                assigned_col[i] = true;
                assigned_row[minCol[i]] = true;
                pairs.add(new pair(c.x[minCol[i]], c.y[minCol[i]],c.x2[i], c.y2[i],  minCol[i],i));
            }

            IJ.showProgress(i,minRow.length-1);

        }


        return pairs;



    }


    private ArrayList<pair> getNNstrickt_twosets(coordinates c){


        ArrayList<pair> pairs = new ArrayList<>();

        if(c.x.length < 1 || c.x2.length < 1){
            pairs.add(new pair(0,0,0,0,-1,-1));
            return pairs;
        }

        boolean[] assigned_row = new boolean[c.x.length];
        boolean[] assigned_col = new boolean[c.x2.length];



        boolean test = false;
        int cnt;
        while(!test) {
            cnt = 0;

            int[] minRow = getMinRow(c.distmat, assigned_row, assigned_col);
            int[] minCol = getMinCol(c.distmat, assigned_row, assigned_col);

            if (c.x.length <= c.x2.length) {



                for(int i=0;i<minRow.length;i++){

                    if(minCol[minRow[i]]== i && !assigned_col[minRow[i]] && !assigned_row[i]){
                        assigned_row[i] = true;
                        assigned_col[minRow[i]] = true;
                        pairs.add(new pair(c.x[i], c.y[i], c.x2[minRow[i]], c.y2[minRow[i]], minRow[i], i));

                    }


                }

                for(boolean j:assigned_row){
                    if(j) {
                        cnt++;
                    }
                }

                if(cnt >= assigned_row.length){
                    test = true;
                }


            } else {



                for(int i=0;i<minCol.length;i++){

                    if(minRow[minCol[i]]== i && !assigned_row[minCol[i]] && !assigned_col[i] ){
                        assigned_row[minCol[i]] = true;
                        assigned_col[i] = true;
                        pairs.add(new pair(c.x[minCol[i]], c.y[minCol[i]], c.x2[i], c.y2[i], minCol[i], i));

                    }



                }

                for(boolean j:assigned_col){
                    if(j) {
                        cnt++;
                    }
                }

                if(cnt >= assigned_col.length){
                    test = true;
                }
            }
        }

        return(pairs);
    }

    private int[] getMinRow(double[][] distmat, boolean[] assigned_row, boolean[] assigned_col){
        int[] minRow = new int[distmat.length];
        double mindist;

        for(int i=0;i<distmat.length;i++){

            mindist = c.max+1;
            for(int j=0;j<distmat[i].length;j++){

                if(!assigned_row[i] && !assigned_col[j]){
                    if(distmat[i][j]<mindist){
                        mindist = distmat[i][j];
                        minRow[i] = j;

                    }
                }
            }
        }


        return minRow;

    }

    private int[] getMinCol(double[][] distmat,boolean[] assigned_row, boolean[] assigned_col){
        int[] minCol = new int[distmat[0].length];
        double mindist;

        for(int i=0;i<distmat[0].length;i++){
            mindist = c.max+1;
            for(int j=0;j<distmat.length;j++){
                if(!assigned_col[i] && !assigned_row[j]){
                    if(distmat[j][i]<mindist){
                        mindist = distmat[j][i];
                        minCol[i] = j;
                    }
                }
            }
        }

        return minCol;

    }




}
