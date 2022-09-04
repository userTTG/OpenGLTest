attribute vec4 vPosition;
attribute vec2 aTextureCoord;
uniform mat4 u_Matrix;
varying  vec2 vTexCoord;
void main() {
    gl_Position  = u_Matrix*vPosition;
    vTexCoord = aTextureCoord;
}


