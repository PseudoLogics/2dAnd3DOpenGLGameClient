import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OBJModel {
    public List<float[]> vertices = new ArrayList<>();
    public List<float[]> textureCoordinates = new ArrayList<>();
    public List<int[][]> texturedFaces = new ArrayList<>();
    public String path;

    public OBJModel(String path) throws IOException {
        this.path = path;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {

                // Vertex positions
                if (line.startsWith("v ")) {
                    String[] tokens = line.trim().split("\\s+");
                    if (tokens.length >= 4) {
                        float x = Float.parseFloat(tokens[1]);
                        float y = Float.parseFloat(tokens[2]);
                        float z = Float.parseFloat(tokens[3]);
                        vertices.add(new float[]{x, y, z});
                    }
                }

                // Texture coordinates
                else if (line.startsWith("vt ")) {
                    String[] tokens = line.trim().split("\\s+");
                    if (tokens.length >= 3) {
                        float u = Float.parseFloat(tokens[1]);
                        float v = Float.parseFloat(tokens[2]);
                        textureCoordinates.add(new float[]{u, v});
                    }
                }

                // Faces
                else if (line.startsWith("f ")) {
                    String[] tokens = line.trim().split("\\s+");

                    // Triangulate quads if needed (we only handle triangles)
                    if (tokens.length < 4) continue;

                    // Parse first 3 vertices as a triangle
                    int[][] face = new int[3][2];
                    for (int i = 1; i <= 3; i++) {
                        String[] parts = tokens[i].split("/");

                        // Parse vertex index
                        int vertexIndex = Integer.parseInt(parts[0]) - 1;

                        // Parse texture coordinate index safely
                        int texCoordIndex = -1;
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            try {
                                texCoordIndex = Integer.parseInt(parts[1]) - 1;
                            } catch (NumberFormatException ignored) {
                                texCoordIndex = -1;
                            }
                        }

                        face[i - 1][0] = vertexIndex;
                        face[i - 1][1] = texCoordIndex;
                    }
                    texturedFaces.add(face);
                }
            }
        }

        if (vertices.isEmpty()) {
            throw new IOException("No vertices found in OBJ file: " + path);
        }

        if (texturedFaces.isEmpty()) {
            System.out.println("Warning: No faces parsed from " + path);
        }
    }
}
