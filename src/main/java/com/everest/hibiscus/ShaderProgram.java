package com.everest.hibiscus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.lwjgl.opengl.GL20;

public class ShaderProgram {
    private final int programId;

    public ShaderProgram(String name) throws IOException {
        String vertSrc = readResource("shaders/" + name + ".vert");
        String fragSrc = readResource("shaders/" + name + ".frag");

        int vertexShader = compileShader(vertSrc, GL20.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragSrc, GL20.GL_FRAGMENT_SHADER);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShader);
        GL20.glAttachShader(programId, fragmentShader);
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0)
            throw new IOException("Shader linking failed: " + GL20.glGetProgramInfoLog(programId));

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    private int compileShader(String src, int type) throws IOException {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, src);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0)
            throw new IOException("Shader compile error: " + GL20.glGetShaderInfoLog(shader));

        return shader;
    }

    private String readResource(String path) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IOException("Resource not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public int getId() {
        return programId;
    }
}
