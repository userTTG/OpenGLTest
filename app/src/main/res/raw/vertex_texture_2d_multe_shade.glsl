attribute vec4 vPosition;
attribute vec2 aTextureCoord;
attribute vec2 aTextureCoord2;
uniform mat4 u_Matrix;
varying  vec2 vTexCoord;
varying  vec2 vTexCoord2;
void main() {
    gl_Position  = u_Matrix*vPosition;
    vTexCoord = aTextureCoord;
    vTexCoord2 = aTextureCoord2;
}


