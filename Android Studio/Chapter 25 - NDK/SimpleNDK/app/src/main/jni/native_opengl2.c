#include <jni.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#define DEBUG_TAG "NDK_NativeOpenGL2Activity"

const GLfloat gVertices[] =  {
    0.0f, 0.5f, 0.0f,
    -0.5f, -0.5f, 0.0f,
    0.5f, -0.5f, 0.0f
};

void Java_com_advancedandroidbook_simplendk_NativeOpenGL2Activity_drawFrame
    (JNIEnv * env, jobject this, jint shaderProgram)
{   
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(shaderProgram);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 12, gVertices);
    glEnableVertexAttribArray(0);
    glDrawArrays(GL_TRIANGLES, 0, 3);
}
