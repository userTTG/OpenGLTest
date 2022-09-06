precision mediump float;
uniform sampler2D uTextureUnit;
uniform sampler2D uTextureUnit2;
varying  vec2 vTexCoord;
varying  vec2 vTexCoord2;
void main() {
//    gl_FragColor = texture2D(uTextureUnit,vTexCoord) + texture2D(uTextureUnit2,vTexCoord2);
//    gl_FragColor = texture2D(uTextureUnit,vTexCoord);
    gl_FragColor = texture2D(uTextureUnit2,vTexCoord2);
}

