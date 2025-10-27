import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.stb.STBImage;


import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class ThreeDGamePanel {

    // The window handle
    private long window;

    public boolean hasSpawnedLocalClient = false;
    public TexturedModel localPlayer;

    public TexturedModel landScape;
    public TexturedModel ogre;

    public float zDepth = -30.0f;

    public static List<TexturedModel> modelList = new ArrayList<>();

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(800, 600, "MICRO MMO", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        if(!hasSpawnedLocalClient) {
            try {
                localPlayer = new TexturedModel("Models/Char.obj", "Models/Char.png", 0, 0, 0, true, UUID.randomUUID());
                hasSpawnedLocalClient = true;
                modelList.add(localPlayer);
                System.out.println("Loaded local Player");
                ServerHandlerThreeD.localPlayer = localPlayer;
            } catch (IOException e) {
                System.out.println("Failed to load local Player");
            }
            //Load landscape for local player
            try{
                landScape = new TexturedModel("Models/Terrain.obj", "Models/Landscape/PlaneGround.png", 0, 0, 0);
                System.out.println("Loaded Landscape");
            }catch (IOException e){
                System.out.println("Failed to load landscape");
            }
//            try{
//                ogre = new TexturedModel("Models/Ogre.obj", "Models/Ogre.png", 0,0,0);
//                System.out.println("Loaded Ogre");
//            }   catch(IOException e){
//                System.out.println("Failed to load ogre");
//            }

        }



        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        float camX = 0, camY = 0, camZ = 0;

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        glEnable(GL_TEXTURE_2D);

        glEnable(GL_DEPTH_TEST);
        // Set the clear color
        glClearColor(0.0f, 0.0f, 1.0f, 0.0f);



        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = 800f / 600f;
        float near = 0.1f;
        float far = 100.0f;
        float fov = 45.0f;
        float top = (float) Math.tan(Math.toRadians(fov / 2)) * near;
        float bottom = -top;
        float right = top * aspect;
        float left = -right;
        glFrustum(left, right, bottom, top, near, far);




        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            for (TexturedModel tm : modelList) {
                if(tm.isLocal){
                    move();
                    tm.y += dirY;
                    tm.x += dirX;
                    tm.z += dirZ;

                    ServerHandlerThreeD.sendMovement3D(tm.x, tm.y, tm.z);
                }

                if(!tm.hasTextureBeenLoaded) {
                    tm.loadTexture();
                    tm.hasTextureBeenLoaded = true;
                }

            }

            if (!landScape.hasTextureBeenLoaded) {
                landScape.loadTexture();
                landScape.hasTextureBeenLoaded = true;
            }
//
//            if (!ogre.hasTextureBeenLoaded) {
//                ogre.loadTexture();
//                ogre.hasTextureBeenLoaded = true;
//            }


            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            float smoothing = 0.1f;
            camX += (localPlayer.x - camX) * smoothing;
            camY += (localPlayer.y - camY + 5.0f) * smoothing;
            camZ += (localPlayer.z - camZ + 12.0f) * smoothing;

            glRotatef(10.0f, 1.0f, 0.0f, 0.0f);
            glTranslatef(-camX, -camY, -camZ);



            for (TexturedModel tm : modelList) {
                glPushMatrix();

                glTranslatef(tm.x, tm.y , tm.z);
                glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                glBindTexture(GL_TEXTURE_2D, tm.textureID);

                glBegin(GL_TRIANGLES);
                for (int[][] face : tm.model.texturedFaces) {
                    for (int[] pair : face) {
                        float[] vertex = tm.model.vertices.get(pair[0]);
                        float[] texCoord = tm.model.textureCoordinates.get(pair[1]);
                        glTexCoord2f(texCoord[0], texCoord[1]);
                        glVertex3f(vertex[0], vertex[1], vertex[2]);
                    }
                }
                glEnd();
                glPopMatrix();
            }

            if (landScape != null) {
                glPushMatrix();

                glTranslatef(landScape.x, landScape.y, landScape.z);
                glBindTexture(GL_TEXTURE_2D, landScape.textureID);

                glBegin(GL_TRIANGLES);
                for (int[][] face : landScape.model.texturedFaces) {
                    for (int[] pair : face) {
                        float[] vertex = landScape.model.vertices.get(pair[0]);
                        float[] texCoord = landScape.model.textureCoordinates.get(pair[1]);
                        glTexCoord2f(texCoord[0], texCoord[1]);
                        glVertex3f(vertex[0], vertex[1], vertex[2]);
                    }
                }
                glEnd();
                glPopMatrix();
            }

//            if (ogre != null) {
//                glPushMatrix();
//                glScalef(3,3,3);
//                glTranslatef(ogre.x, ogre.y, ogre.z);
//                glBindTexture(GL_TEXTURE_2D, ogre.textureID);
//
//                glBegin(GL_TRIANGLES);
//                for (int[][] face : ogre.model.texturedFaces) {
//                    for (int[] pair : face) {
//                        float[] vertex = ogre.model.vertices.get(pair[0]);
//                        float[] texCoord = ogre.model.textureCoordinates.get(pair[1]);
//                        glTexCoord2f(texCoord[0], texCoord[1]);
//                        glVertex3f(vertex[0], vertex[1], vertex[2]);
//                    }
//                }
//                glEnd();
//                glPopMatrix();
//            }



            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

        }
    }

    public static int loadTextureStatic(String path){
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 4);
            if (image == null) throw new RuntimeException("Failed to load texture");

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);
        }

        return textureID;
    }



    float dirZ, dirX, dirY;
    float speed = .1f;

    public void move() {

        dirX = 0;
        dirY = 0;
        dirZ = 0;

        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            dirZ = speed;
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            dirZ = -speed;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            dirX = -speed;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            dirX = speed;
        }
    }


    }













































































































































































































































