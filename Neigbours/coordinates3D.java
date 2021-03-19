package Neigbours;

public class coordinates3D extends coordinates{



        double[] z,z2;

        coordinates3D(double[] x, double[] y, double[] z){

            super(x,y);
            this.z = z;
            addZdist(z,z);


        }

        coordinates3D(double[] x1, double[] y1,double[] z1, double[] x2, double[] y2, double[] z2){

            super(x1,y1,x2,y2);
            this.z = z1;
            this.z2 = z2;
            addZdist(z1,z2);

        }

        private void addZdist(double[] z1, double[] z2 ){

            super.max=0;

            for(int i=0;i<super.distmat.length;i++){
                for(int j=0;j<super.distmat[0].length;j++){
                    super.distmat[i][j] = Math.sqrt( Math.pow(super.distmat[i][j],2) + Math.pow(z1[i]-z2[j],2) );
                    super.max = Math.max(super.distmat[i][j],super.max);
                }
            }
        }


}
