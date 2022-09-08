precision mediump float;
uniform sampler2D uTextureUnit;
uniform sampler2D uTextureUnit2;
varying  vec2 vTexCoord;
varying  vec2 vTexCoord2;

bool isOutRect(vec2 coord) {
    return coord.x >coord.y;
}

void main() {
    gl_FragColor = texture2D(uTextureUnit,vTexCoord);
    if(isOutRect(vTexCoord2)){
        gl_FragColor = texture2D(uTextureUnit2,vTexCoord2);
    }else{
        gl_FragColor = texture2D(uTextureUnit,vTexCoord);
    }
}

