package com.github.ntsee.sheetchef.exceptions;

import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;

public class FileFormatException extends IOException {

    public FileFormatException(FileHandle handle, String message) {
        super(String.format("%s - %s", handle, message));
    }
}
