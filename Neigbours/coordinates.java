package Neigbours;

public class coordinates {

    double[] x,y,x2,y2;
    double max;
    double[][] distmat;

    coordinates(double[] x, double[] y){

        this.x = x;
        this.y = y;

        MakeDistmat(x,y,x,y);

    }

    coordinates(double[] x1, double[] y1,double[] x2, double[] y2){

        this.x = x1;
        this.y = y1;
        this.x2 = x2;
        this.y2 = y2;

        MakeDistmat(x1,y1,x2,y2);

    }



    private void MakeDistmat(double[] x1, double[] y1, double[] x2, double[] y2){

        double[][] dmat = new double[x1.length][x2.length];
        this.max = 0;

        for(int i=0;i<x1.length;i++){
            for(int j=0;j<x2.length;j++){
                dmat[i][j] = Math.sqrt( Math.pow(x1[i]-x2[j],2) + Math.pow(y1[i]-y2[j],2) );

                this.max = Math.max(dmat[i][j],this.max);
            }
        }

        this.distmat = dmat;
    }

}


