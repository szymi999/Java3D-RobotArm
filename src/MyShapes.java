// klasa w której tworzymy własne kształty

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

import javax.media.j3d.*;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class MyShapes {

    public Shape3D makeCuboid(float length, float width, float height){
        Point3f a = new Point3f(-length/2, 0.0f, width/2);
        Point3f b = new Point3f(length/2, 0.0f, width/2);
        Point3f c = new Point3f(length/2, 0.0f, -width/2);
        Point3f d = new Point3f(-length/2, 0.0f, -width/2);

        Point3f e = new Point3f(-length/2, height, width/2);
        Point3f f = new Point3f(length/2, height, width/2);
        Point3f g = new Point3f(length/2, height, -width/2);
        Point3f h = new Point3f(-length/2, height, -width/2);

        TriangleArray obj = new TriangleArray(36, TriangleArray.COORDINATES);

        obj.setCoordinate(0, b);
        obj.setCoordinate(1, a);
        obj.setCoordinate(2, d);

        obj.setCoordinate(3, c);
        obj.setCoordinate(4, b);
        obj.setCoordinate(5, d);

        obj.setCoordinate(6, b);
        obj.setCoordinate(7, c);
        obj.setCoordinate(8, f);

        obj.setCoordinate(9, c);
        obj.setCoordinate(10, g);
        obj.setCoordinate(11, f);

        obj.setCoordinate(12, a);
        obj.setCoordinate(13, b);
        obj.setCoordinate(14, e);

        obj.setCoordinate(15, b);
        obj.setCoordinate(16, f);
        obj.setCoordinate(17, e);

        obj.setCoordinate(18, d);
        obj.setCoordinate(19, a);
        obj.setCoordinate(20, e);

        obj.setCoordinate(21, e);
        obj.setCoordinate(22, h);
        obj.setCoordinate(23, d);

        obj.setCoordinate(24, c);
        obj.setCoordinate(25, d);
        obj.setCoordinate(26, g);

        obj.setCoordinate(27, d);
        obj.setCoordinate(28, h);
        obj.setCoordinate(29, g);

        obj.setCoordinate(30, e);
        obj.setCoordinate(31, f);
        obj.setCoordinate(32, g);

        obj.setCoordinate(33, e);
        obj.setCoordinate(34, g);
        obj.setCoordinate(35, h);

        GeometryInfo geoInfo = new GeometryInfo(obj);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(geoInfo);

        GeometryArray result = geoInfo.getGeometryArray();

        return new Shape3D(result);
    }

    public Shape3D makeTriangularShape(float length, float width, float height, float zWidth){
        Point3f a = new Point3f(-length/2, height-height, zWidth/2);
        Point3f b = new Point3f(length/2, height-height, zWidth/2);
        Point3f c = new Point3f(length/2, height-width, zWidth/2);
        Point3f d = new Point3f((length/2-(width)), height, zWidth/2);
        Point3f e = new Point3f(-(length/2-(width)), height, zWidth/2);
        Point3f f = new Point3f(-length/2, height-width, zWidth/2);

        Point3f g = new Point3f(-length/2, height-height, -zWidth/2);
        Point3f h = new Point3f(length/2, height-height, -zWidth/2);
        Point3f i = new Point3f(length/2, height-width, -zWidth/2);
        Point3f j = new Point3f((length/2-(width)), height, -zWidth/2);
        Point3f k = new Point3f(-(length/2-(width)), height, -zWidth/2);
        Point3f l = new Point3f(-length/2, height-width, -zWidth/2);

        TriangleArray obj = new TriangleArray(60, TriangleArray.COORDINATES);

        obj.setCoordinate(0, a);
        obj.setCoordinate(1, e);
        obj.setCoordinate(2, f);

        obj.setCoordinate(3, a);
        obj.setCoordinate(4, d);
        obj.setCoordinate(5, e);

        obj.setCoordinate(6, a);
        obj.setCoordinate(7, c);
        obj.setCoordinate(8, d);

        obj.setCoordinate(9, a);
        obj.setCoordinate(10, b);
        obj.setCoordinate(11, c);

        obj.setCoordinate(12, h);
        obj.setCoordinate(13, j);
        obj.setCoordinate(14, i);

        obj.setCoordinate(15, h);
        obj.setCoordinate(16, k);
        obj.setCoordinate(17, j);

        obj.setCoordinate(18, h);
        obj.setCoordinate(19, l);
        obj.setCoordinate(20, k);

        obj.setCoordinate(21, h);
        obj.setCoordinate(22, g);
        obj.setCoordinate(23, l);

        obj.setCoordinate(24, a);
        obj.setCoordinate(25, g);
        obj.setCoordinate(26, b);

        obj.setCoordinate(27, h);
        obj.setCoordinate(28, b);
        obj.setCoordinate(29, g);

        obj.setCoordinate(30, b);
        obj.setCoordinate(31, h);
        obj.setCoordinate(32, c);

        obj.setCoordinate(33, i);
        obj.setCoordinate(34, c);
        obj.setCoordinate(35, h);

        obj.setCoordinate(36, c);
        obj.setCoordinate(37, i);
        obj.setCoordinate(38, d);

        obj.setCoordinate(39, i);
        obj.setCoordinate(40, j);
        obj.setCoordinate(41, d);

        obj.setCoordinate(42, d);
        obj.setCoordinate(43, j);
        obj.setCoordinate(44, e);

        obj.setCoordinate(45, j);
        obj.setCoordinate(46, k);
        obj.setCoordinate(47, e);

        obj.setCoordinate(48, e);
        obj.setCoordinate(49, k);
        obj.setCoordinate(50, f);

        obj.setCoordinate(51, k);
        obj.setCoordinate(52, l);
        obj.setCoordinate(53, f);

        obj.setCoordinate(54, f);
        obj.setCoordinate(55, l);
        obj.setCoordinate(56, a);

        obj.setCoordinate(57, l);
        obj.setCoordinate(58, g);
        obj.setCoordinate(59, a);

        GeometryInfo geoInfo = new GeometryInfo(obj);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(geoInfo);

        GeometryArray result = geoInfo.getGeometryArray();

        return new Shape3D(result);
    }

    public Shape3D makeCustomCylinder(float lowerRadius, float upperRadius, float height, int n){

        // make points
        Point3f[] points = new Point3f[n];

        for(int i=0; i<n/2; i++){
            points[i] = new Point3f(new Vector3f((float)(lowerRadius * Math.cos(Math.PI * 2 * i / (n/2))),
                    0, (float)(lowerRadius * Math.sin(Math.PI * 2 * i / (n/2)))));
        }

        for(int i=n/2; i<n; i++){
            points[i] = new Point3f(new Vector3f((float)(upperRadius * Math.cos(Math.PI * 2 * i / (n/2))),
                    height, (float)(upperRadius * Math.sin(Math.PI * 2 * i / (n/2)))));
        }

        int coordinates = (n/2 - 2 + n/2 - 2 + n) * 3;
        TriangleArray obj = new TriangleArray(coordinates, TriangleArray.COORDINATES);

        // lower circle
        int count = 0;
        for(int i=0; i<(n/2-2)*3; i+=3){
            obj.setCoordinate(i, points[0]);
            obj.setCoordinate(i + 1, points[count+1]);
            obj.setCoordinate(i + 2, points[count+2]);
            count++;
        }

        // bigger circle
        count = n/2;
        for(int i=(n/2-2)*3; i<(n-4)*3; i+=3){
            obj.setCoordinate(i, points[n/2]);
            obj.setCoordinate(i + 1, points[count+2]);
            obj.setCoordinate(i + 2, points[count+1]);
            count++;
        }

        // walls
        count = 0;
        for(int i=(n-4)*3; i<coordinates; i+=6){
            obj.setCoordinate(i, points[count]);
            if(count<(n/2)-1)
                obj.setCoordinate(i+1, points[count + (n/2) + 1]);
            else
                obj.setCoordinate(i+1, points[count + 1]);
            if(count<(n/2)-1)
                obj.setCoordinate(i+2, points[count + 1]);
            else
                obj.setCoordinate(i+2, points[0]);

            obj.setCoordinate(i+3, points[count]);
            obj.setCoordinate(i+4, points[count + (n/2)]);
            if(count<(n/2)-1)
                obj.setCoordinate(i+5, points[count + (n/2) + 1]);
            else
                obj.setCoordinate(i+5, points[n/2]);
            count++;
        }

        GeometryInfo geoInfo = new GeometryInfo(obj);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(geoInfo);

        GeometryArray result = geoInfo.getGeometryArray();

        return new Shape3D(result);
    }

    public Shape3D makeGround(Point3f rightUp, Point3f rightDown, Point3f leftDown, Point3f leftUp){
        Point3f[]  coords = new Point3f[8];
        for(int i = 0; i< 8; i++)
            coords[i] = new Point3f();

        Point2f[]  tex_coords = new Point2f[8];
        for(int i = 0; i< 8; i++)
            tex_coords[i] = new Point2f();

        coords[0] = rightUp;
        coords[1] = rightDown;
        coords[2] = leftDown;
        coords[3] = leftUp;

        coords[4] = rightDown;
        coords[5] = rightUp;
        coords[6] = leftUp;
        coords[7] = leftDown;

        tex_coords[0].x = 0.0f;
        tex_coords[0].y = 0.0f;

        tex_coords[1].x = 10.0f;
        tex_coords[1].y = 0.0f;

        tex_coords[2].x = 0.0f;
        tex_coords[2].y = 10.0f;

        tex_coords[3].x = 10.0f;
        tex_coords[3].y = 10.0f;

        tex_coords[4].x = 0.0f;
        tex_coords[4].y = 0.0f;

        tex_coords[5].x = 10.0f;
        tex_coords[5].y = 0.0f;

        tex_coords[6].x = 0.0f;
        tex_coords[6].y = -10.0f;

        tex_coords[7].x = 10.0f;
        tex_coords[7].y = -10.0f;

        QuadArray qa_ground = new QuadArray(8, GeometryArray.COORDINATES|
                GeometryArray.TEXTURE_COORDINATE_2);
        qa_ground.setCoordinates(0,coords);

        qa_ground.setTextureCoordinates(0, tex_coords);

        Shape3D ground = new Shape3D(qa_ground);

        return ground;
    }

}
