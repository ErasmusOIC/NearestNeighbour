package Neigbours;

public class pair {
    public double x1,y1,z1,x2,y2,z2,dist;
    public int i1,i2;
    boolean is3D;


    public pair(double x1,double y1, double x2, double y2, int i1, int i2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.i1 = i1;
        this.i2 = i2;
        this.is3D = false;
        if(this.i1<0 || this.i2<0){
            this.dist = -1;
        }else {
            this.dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        }
    }

    public pair(double x1,double y1, double z1, double x2, double y2, double z2, int i1, int i2){
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.i1 = i1;
        this.i2 = i2;
        this.is3D = true;
        if(this.i1<0 || this.i2<0){
            this.dist = -1;
        }else {
            this.dist = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)+Math.pow(z1-z2,2));
        }

    }

}
