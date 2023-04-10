package com.github.ntsee.sheetchef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.ntsee.sheetchef.exceptions.FileFormatException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageSlicer implements Disposable {

    private static final String EXPORT_DIRECTORY = "output";

    private Pixmap inputImage;
    private final Color backgroundColor = new Color(Color.WHITE);
    private final List<Rectangle> boxes = new ArrayList<>();
    private ExportFormat exportFormat = ExportFormat.BMP;
    private FileHandle exportDirectory = Gdx.files.local(EXPORT_DIRECTORY);

    public void setInputImage(FileHandle handle) throws FileFormatException {
        Pixmap pixmap;
        try {
            pixmap = new Pixmap(handle);
        } catch (GdxRuntimeException e) {
            throw new FileFormatException(handle, "could not load image");
        }

        if (this.inputImage != null) {
            this.inputImage.dispose();
        }

        this.inputImage = pixmap;
        this.calculateSubImageBoxes();
    }

    private void calculateSubImageBoxes() {
        this.boxes.clear();
        int background = Color.rgba8888(this.backgroundColor);
        for (int y=0; y<this.inputImage.getHeight(); y++) {
            for (int x=0; x<this.inputImage.getWidth(); x++) {
                if (this.isSubImageStart(x, y, background)) {
                    int width = this.getSubImageWidth(x, y, background);
                    int height = this.getSubImageHeight(x, y, background);
                    Rectangle box = new Rectangle(x, y, width, height);
                    this.boxes.add(box);
                }
            }
        }
    }

    private boolean isSubImageStart(int x, int y, int background) {
        return this.inputImage.getPixel(x, y) != background
                && (x - 1 < 0 || this.inputImage.getPixel(x - 1, y) == background)
                && (y - 1 < 0 || this.inputImage.getPixel(x, y - 1) == background);
    }

    private int getSubImageWidth(int startX, int startY, int background) {
        int width = 0;
        while (startX < this.inputImage.getWidth()
                && this.inputImage.getPixel(startX, startY) != background) {
            startX++;
            width++;
        }

        return width;
    }

    private int getSubImageHeight(int startX, int startY, int background) {
        int height = 0;
        while (startY < this.inputImage.getHeight()
                && this.inputImage.getPixel(startX, startY) != background) {
            startY++;
            height++;
        }

        return height;
    }

    public Pixmap getInputImage() {
        return this.inputImage;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor.set(color);
        if (this.inputImage != null) {
            this.calculateSubImageBoxes();
        }
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setExportFormat(ExportFormat exportFormat) {
        this.exportFormat = exportFormat;
    }

    public ExportFormat getExportFormat() {
        return this.exportFormat;
    }

    public void setExportDirectory(FileHandle exportDirectory) throws FileFormatException {
        if (!exportDirectory.isDirectory()) {
            throw new FileFormatException(exportDirectory, "invalid directory");
        }

        this.exportDirectory = exportDirectory;
    }

    public FileHandle getExportDirectory() {
        return this.exportDirectory;
    }

    public List<Rectangle> getBoxes() {
        return this.boxes;
    }

    public void export() throws IOException {
        for (int i = 0; i < this.boxes.size(); i++) {
            String fileName = String.format("%d.%s", i, this.exportFormat.name().toLowerCase());
            FileHandle outputHandle = this.exportDirectory.child(fileName);
            BufferedImage image = this.createSubImage(this.boxes.get(i));
            if (!ImageIO.write(image, this.exportFormat.name(), outputHandle.file())) {
                throw new IOException("Failed to save " + fileName);
            }
        }
    }

    private BufferedImage createSubImage(Rectangle box) {
        BufferedImage image = new BufferedImage((int)box.width, (int)box.height, BufferedImage.TYPE_INT_RGB);
        for (int y=0; y<box.height; y++) {
            for(int x=0; x<box.width; x++) {
                int i = (int)box.x + x;
                int j = (int)box.y + y;
                int pixel = this.inputImage.getPixel(i, j) >> 8;
                image.setRGB(x, y, pixel);
            }
        }

        return image;
    }

    @Override
    public void dispose() {
        if (this.inputImage != null) {
            this.inputImage.dispose();
        }
    }
}
