import java.io.IOException;
import java.util.UUID;

public class TexturedModel {

    public OBJModel model;
    public String texturePath;
    public int textureID;
    public float x, y, z;
    public boolean isLocal;
    public UUID uuid;
    public boolean hasTextureBeenLoaded;

    public  TexturedModel(String modelPath, String texturePath, float x, float y, float z, boolean isLocal, UUID uuid) throws IOException {
        this.model = new OBJModel(modelPath);
        this.texturePath = texturePath;
        this.textureID = -1;
        this.x = x;
        this.y = y;
        this.z = z;
        this.isLocal = isLocal;
        this.uuid = uuid;
        hasTextureBeenLoaded = false;
    }

    public TexturedModel(String modelPath, String texturePath, float x, float y, float z) throws IOException {
        this.model = new OBJModel(modelPath);
        this.texturePath = texturePath;
        this.x = x;
        this.y = y;
        this.z = z;
        hasTextureBeenLoaded = false;
    }

    public void loadTexture(){
        this.textureID = ThreeDGamePanel.loadTextureStatic(texturePath);
    }
}
