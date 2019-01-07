import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;

import java.io.IOException;

public class model3D implements GLEventListener {

    modelInfo model;
    float xrt=0, yrt=0, zrt=0;
    float xmov=0, ymov=0;
    float scalerate = 1.0f;
    private GLU glu = new GLU();
    public model3D(String filename)
    {
        try {
            model = offLoader.loadfile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        float[] light1 = { 1.0f, 0.7f, 0.7f, 1.0f };
        float[] light2 = { 0.7f, 1.0f, 0.7f, 1.0f };
        float[] light3 = { 0.7f, 0.7f, 1.0f, 1.0f };
        float[] light4 = { 0.5f, 0.5f, 0.5f, 1.0f };
        float[] pos1 = {-50.0f, -50.0f, -50.0f, 1.0f};
        float[] pos2 = {50.0f, 50.0f, -50.0f, 1.0f};
        float[] pos3 = {50.0f, -50.0f, 50.0f, 1.0f};
        float[] pos4 = {-50.0f, 50.0f, 50.0f, 1.0f};
        float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, light1,0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos1, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, light2,0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, pos2, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, light3,0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, pos3, 0);
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_DIFFUSE, light4,0);
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT4, GL2.GL_POSITION, pos4, 0);
//        gl.glEnable(GL2.GL_LIGHT1);
//        gl.glEnable(GL2.GL_LIGHT2);
//        gl.glEnable(GL2.GL_LIGHT3);
//        gl.glEnable(GL2.GL_LIGHT4);
//        gl.glEnable( GL2.GL_LIGHTING );
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        // Clear The Screen And The Depth Buffer
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity(); // Reset The View
        gl.glTranslatef( -0.2f+xmov, 0.0f+ymov, -3.0f ); // Move the triangle
        gl.glRotatef( xrt, 1.0f, 0.0f, 0.0f );
        gl.glRotatef(yrt, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(zrt, 0.0f, 0.0f, 1.0f);
        gl.glScalef(scalerate, scalerate, scalerate);

        gl.glBegin( GL2.GL_TRIANGLES );
        gl.glColor3f( 0.6f, 0.6f, 0.6f ); // Blue

        for(int i=0;i<model.surfacenum;i++)
        {
            int[] ps = model.surfaceSet[i];
            gl.glVertex3f((float)model.vertexSet[ps[0]][0], (float)model.vertexSet[ps[0]][1], (float)model.vertexSet[ps[0]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[1]][0], (float)model.vertexSet[ps[1]][1], (float)model.vertexSet[ps[1]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[2]][0], (float)model.vertexSet[ps[2]][1], (float)model.vertexSet[ps[2]][2]);
        }
        gl.glEnd(); // Done Drawing 3d triangle (Pyramid)

        gl.glColor3f( 1.0f, 0.0f, 0.0f ); // Red
        gl.glBegin( GL2.GL_LINES);
        for(int i=0;i<model.surfacenum;i++)
        {
            int[] ps = model.surfaceSet[i];
            gl.glVertex3f((float)model.vertexSet[ps[0]][0], (float)model.vertexSet[ps[0]][1], (float)model.vertexSet[ps[0]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[1]][0], (float)model.vertexSet[ps[1]][1], (float)model.vertexSet[ps[1]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[1]][0], (float)model.vertexSet[ps[1]][1], (float)model.vertexSet[ps[1]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[2]][0], (float)model.vertexSet[ps[2]][1], (float)model.vertexSet[ps[2]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[2]][0], (float)model.vertexSet[ps[2]][1], (float)model.vertexSet[ps[2]][2]);
            gl.glVertex3f((float)model.vertexSet[ps[0]][0], (float)model.vertexSet[ps[0]][1], (float)model.vertexSet[ps[0]][2]);
        }
        gl.glEnd();
        gl.glFlush();
//        System.out.printf("%f, %f\n", xrt, yrt);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        // TODO Auto-generated method stub final
        GL2 gl = glAutoDrawable.getGL().getGL2();
        if(height <= 0)
        height = 1;

        final float h = (float) width / (float) height;
        gl.glViewport(3, 6, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}


