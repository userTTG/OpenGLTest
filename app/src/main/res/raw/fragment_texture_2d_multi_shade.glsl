precision mediump float;
uniform sampler2D uTextureUnit;
uniform sampler2D uTextureUnit2;
varying  vec2 vTexCoord;
varying  vec2 vTexCoord2;

bool isOutRect(vec2 coord) {
    return coord.x < 0.0 || coord.x > 1.0 || coord.y < 0.0 || coord.y > 1.0;
}

void main() {
//    gl_FragColor = texture2D(uTextureUnit,vTexCoord) + texture2D(uTextureUnit2,vTexCoord2);
    if(isOutRect(vTexCoord2)){
        gl_FragColor = texture2D(uTextureUnit,vTexCoord);
    }else{
        gl_FragColor = texture2D(uTextureUnit2,vTexCoord2);
    }
//    gl_FragColor = texture2D(uTextureUnit,vTexCoord);
//    gl_FragColor = texture2D(uTextureUnit2,vTexCoord2);
}

