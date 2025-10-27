import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public float x, y, z;
    public float pitch, yaw, roll;
    private Matrix4f viewMatrix = new Matrix4f();


    public Matrix4f getViewMatrix(){
        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(pitch), new Vector3f(1,0,0));
        viewMatrix.rotate((float)Math.toRadians(yaw), new Vector3f(0,1,0));
        viewMatrix.rotate((float)Math.toRadians(roll), new Vector3f(0,0,1));
        viewMatrix.translate(-x, -y, -z);
        return viewMatrix;

    }

    public Matrix4f lookAt(Vector3f target){
        return viewMatrix.identity().lookAt(new Vector3f(x,y,z), target, new Vector3f(0,1,0));

    }

}
